package fr.openmc.core.utils.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class ParticleUtils {

    private static final double MAX_PARTICLE_DISTANCE_SQR = 100 * 100;

    public static Color color1;
    public static Color color2;

    // based on org.bukkit.craftbukkit.CraftParticle
    private static final Map<String, Supplier<Object>> PARTICLE_FALLBACKS;

    static {
        PARTICLE_FALLBACKS = new HashMap<>();
        PARTICLE_FALLBACKS.put("dust", () -> new Particle.DustOptions(Color.RED, 1.0f));
        PARTICLE_FALLBACKS.put("block", Material.STONE::createBlockData);
        PARTICLE_FALLBACKS.put("falling_dust", Material.STONE::createBlockData);
        PARTICLE_FALLBACKS.put("block_marker", Material.STONE::createBlockData);
        PARTICLE_FALLBACKS.put("dust_pillar", Material.STONE::createBlockData);
        PARTICLE_FALLBACKS.put("block_crumble", Material.STONE::createBlockData);
        PARTICLE_FALLBACKS.put("item", () -> new ItemStack(Material.STONE));
        PARTICLE_FALLBACKS.put("dust_color_transition", () -> new Particle.DustTransition(Color.RED, Color.BLUE, 1.0f));
        PARTICLE_FALLBACKS.put("sculk_charge", () -> 0.0f);
        PARTICLE_FALLBACKS.put("dragon_breath", () -> 0.0f);
        PARTICLE_FALLBACKS.put("shriek",() -> 0);
        PARTICLE_FALLBACKS.put("entity_effect", () -> Color.WHITE);
        PARTICLE_FALLBACKS.put("tinted_leaves", () -> Color.WHITE);
        PARTICLE_FALLBACKS.put("flash", () -> Color.WHITE);
        PARTICLE_FALLBACKS.put("effect", () -> new Particle.Spell(Color.WHITE, 1.0f));
        PARTICLE_FALLBACKS.put("instant_effect", () -> new Particle.Spell(Color.WHITE, 1.0f));
    }

    public static void sendRandomCubeParticles(Player player, Particle particle, double radius, int amount) {
        Location center = player.getLocation();

        for (int i = 0; i < amount; i++) {
            double x = (Math.random() * 2 - 1) * radius; // de -radius à +radius
            double y = (Math.random() * 2 - 1) * radius;
            double z = (Math.random() * 2 - 1) * radius;

            Location loc = center.clone().add(x, y, z);
            sendParticlePacket(player, particle, loc);
        }
    }

    public static void spawnParticlesInRegion(String regionId, World world, Particle particle, int amountPer2Tick, int minHeight, int maxHeight) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) return;

        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) return;

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        Location minLocation = new Location(world, min.x(), minHeight, min.z());
        Location maxLocation = new Location(world, max.x(), maxHeight, max.z());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(WeeklyEventsManager.getCurrentEvent() instanceof Contest)) return;
                if (WeeklyEventsManager.getCurrentPhase() == ContestPhase.END_PHASE.getPhase()) return;

                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location particleLocation = new Location(world, x, y, z);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.getWorld().equals(world)) continue;

                        if (!region.contains(BukkitAdapter.asBlockVector(player.getLocation()))) continue;

                        sendParticlePacket(player, particle, particleLocation);
                    }
                }
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
    }

    public static void sendParticlePacket(Particle particle, Location loc, int radius) {
        Collection<Player> players = loc.getNearbyEntitiesByType(Player.class, radius);

        for (Player player : players) {
            sendParticlePacket(player, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, null);
        }
    }

    public static void sendParticlePacket(Player player, Particle particle, Location loc) {
        sendParticlePacket(player, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, null);
    }

    public static <T> void sendParticlePacket(Player player, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed, T data) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        // * Fallback pour les data de particule
        Object resolvedData;
        // Si data est une valeur qui est voulu par le dev
        if (data != null) {
            resolvedData = data;
        } else {
            // Si data est null, le dev ne veut rien mettre de spécial, on prend le fallback
            Supplier<Object> fallback = PARTICLE_FALLBACKS.get(particle.getKey().getKey());
            if (fallback != null) {
                resolvedData = fallback.get();
            } else {
                resolvedData = null;
            }
        }

        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                CraftParticle.createParticleParam(particle, resolvedData),
                false,
                false,
                location.x(), location.y(), location.z(),
                (float) offsetX, (float) offsetY, (float) offsetZ,
                (float) speed,
                count
        );

        nmsPlayer.connection.send(packet);
    }

    public static void spawnContestParticlesInRegion(String regionId, World world, int amountPer2Tick, int minHeight, int maxHeight) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) return;

        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) return;

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        Location minLocation = new Location(world, min.x(), minHeight, min.z());
        Location maxLocation = new Location(world, max.x(), maxHeight, max.z());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(WeeklyEventsManager.getCurrentEvent() instanceof Contest)) return;
                if (WeeklyEventsManager.getCurrentPhase() == ContestPhase.END_PHASE.getPhase()) return;

                if (color1 == null || color2 == null) {
                    String camp1Color = ContestManager.data.getColor1();
                    String camp2Color = ContestManager.data.getColor2();

                    if (camp1Color == null || camp1Color.isEmpty()) {
                        camp1Color = "WHITE";
                    }

                    if (camp2Color == null || camp2Color.isEmpty()) {
                        camp2Color = "BLACK";
                    }

                    NamedTextColor colorCamp1 = ColorUtils.getNamedTextColor(camp1Color);
                    NamedTextColor colorCamp2 = ColorUtils.getNamedTextColor(camp2Color);

                    int[] rgb1 = ColorUtils.getRGBFromNamedTextColor(colorCamp1);
                    int[] rgb2 = ColorUtils.getRGBFromNamedTextColor(colorCamp2);

                    color1 = Color.fromRGB(rgb1[0], rgb1[1], rgb1[2]);
                    color2 = Color.fromRGB(rgb2[0], rgb2[1], rgb2[2]);
                }

                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location base = new Location(world, x, y, z);
                    spawnRisingDustParticle(regionId, world, base, color1, 1.0f, 15, 1);
                }

                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location base = new Location(world, x, y, z);
                    spawnRisingDustParticle(regionId, world, base, color2, 1.0f, 15, 1);
                }
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
    }

    public static void spawnRisingDustParticle(String regionId, World world, Location origin, Color color, float size, int steps, int count) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) return;

        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) return;

        Vec3 current = new Vec3(origin.getX(), origin.getY(), origin.getZ());
        Vec3 step = new Vec3(0, 0.10, 0);

        int rgb = color.asRGB();

        DustParticleOptions dust = new DustParticleOptions(rgb, size);

        new BukkitRunnable() {
            int stepCount = 0;
            Vec3 position = current;

            @Override
            public void run() {
                if (stepCount > steps) {
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getWorld().equals(world)) continue;

                    if (!region.contains(BukkitAdapter.asBlockVector(player.getLocation()))) continue;

                    if (player.getLocation().distanceSquared(origin) > MAX_PARTICLE_DISTANCE_SQR) continue;

                    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                    ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                            dust, true, true,
                            position.x, position.y, position.z,
                            0, 0.1f, 0, 0.01f, count
                    );
                    nmsPlayer.connection.send(packet);
                }

                position = position.add(step);
                stepCount++;
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 1L);
    }

    public static void spawnCloudParticles(Location center, Particle particle, int count, double radius, double height) {
        for (Player player : center.getNearbyEntitiesByType(Player.class, radius)) {
            spawnCloudParticles(player, particle, center, count, radius, height);
        }
    }

    public static void spawnCloudParticles(Player player, Particle particle, Location center, int count, double radius, double height) {
        World world = center.getWorld();
        if (world == null) return;
        double minY = center.getY() - Math.abs(height);
        double maxY = center.getY();
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double distance = Math.random() * radius;

            double x = center.getX() + Math.cos(angle) * distance;
            double y = minY + Math.random() * (maxY - minY);
            double z = center.getZ() + Math.sin(angle) * distance;
            Location loc = new Location(world, x, y, z);

            sendParticlePacket(player, particle, loc);
        }
    }

    public static void spawnConvergingParticles(Location target, int count) {
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = random.nextDouble() * 5;

            double offsetX = Math.cos(angle) * radius;
            double offsetY = 2 + random.nextDouble();
            double offsetZ = Math.sin(angle) * radius;

            Particle.ENCHANT.builder()
                    .location(target)
                    .offset(offsetX, offsetY, offsetZ)
                    .count(0)
                    .receivers(32, true)
                    .spawn();
        }
    }

    public static <T> void spawnDispersingParticles(Location target, Particle particle, int count, int radius, double speed, T data) {
        Collection<Player> players = target.getNearbyEntitiesByType(Player.class, radius);

        for (Player player : players) {
            spawnDispersingParticles(player, particle, target, count, speed, data);
        }
    }

    public static <T> void spawnDispersingParticles(Player player, Particle particle, Location target, int count, double speed, T data) {
        ParticleUtils.sendParticlePacket(
                player,
                particle,
                target,
                count,
                0.3D,
                0.2D,
                0.3D,
                speed,
                data
        );
    }
}
