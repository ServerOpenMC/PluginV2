package fr.openmc.core.utils.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.OMCPlugin;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class ParticleUtils {

    /**
     * based on {@link org.bukkit.craftbukkit.CraftParticle}
      */
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
        PARTICLE_FALLBACKS.put("flash", () -> Color.RED);
        PARTICLE_FALLBACKS.put("effect", () -> new Particle.Spell(Color.WHITE, 1.0f));
        PARTICLE_FALLBACKS.put("instant_effect", () -> new Particle.Spell(Color.WHITE, 1.0f));
        PARTICLE_FALLBACKS.put("vibration", () -> new Vibration(
                new Vibration.Destination.BlockDestination(new Location(null, 0, 0, 0)),
                20
        ));
        PARTICLE_FALLBACKS.put("trail", () -> new Particle.Trail(
                new Location(null, 0, 0, 0),
                Color.RED,
                20
        ));
        PARTICLE_FALLBACKS.put("geyser", () -> new Particle.Geyser(1));
        PARTICLE_FALLBACKS.put("geyser_plume", () -> new Particle.Geyser(1));
        PARTICLE_FALLBACKS.put("geyser_base", () -> new Particle.GeyserBase(1, 1.0f));
        PARTICLE_FALLBACKS.put("geyser_poof", () -> new Particle.GeyserBase(1, 1.0f));
    }

    public static void sendParticlePacket(Particle particle, Location loc, int radius) {
        sendParticlePacket(particle, loc, loc.getNearbyEntitiesByType(Player.class, radius));
    }

    public static <T> void sendParticlePacket(Particle particle, Location loc, int radius, T data) {
        sendParticlePacket(particle, loc, loc.getNearbyEntitiesByType(Player.class, radius), data);
    }

    public static <T> void sendParticlePacket(Particle particle, Location loc, Collection<Player> receivers, T data) {
        for (Player player : receivers) {
            sendParticlePacket(player, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, data);
        }
    }

    public static void sendParticlePacket(Particle particle, Location loc, Collection<Player> receivers) {
        for (Player player : receivers) {
            sendParticlePacket(player, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, null);
        }
    }

    public static void sendParticlePacket(Player player, Particle particle, Location loc) {
        sendParticlePacket(player, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, null);
    }

    public static <T> void sendParticlePacket(Collection<Player> receivers, Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed, T data) {
        Object resolvedData;
        if (data != null) {
            resolvedData = data;
        } else {
            Supplier<Object> fallback = PARTICLE_FALLBACKS.get(particle.getKey().getKey());
            if (fallback != null) {
                resolvedData = fallback.get();
            } else {
                resolvedData = null;
            }
        }

        ParticleOptions particleParam = CraftParticle.createParticleParam(particle, resolvedData);

        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                particleParam,
                false,
                false,
                location.x(), location.y(), location.z(),
                (float) offsetX, (float) offsetY, (float) offsetZ,
                (float) speed,
                count
        );

        for (Player player : receivers) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }
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

    public static <T> void spawnParticlesInCube(Location origin, Particle particle, int count, int size, T data) {
        Collection<Player> receivers = origin.getNearbyEntitiesByType(Player.class, 64);
        if (receivers.isEmpty()) return;

        for (int i = 0; i < count; i++) {
            double x = (Math.random() * 2 - 1) * size;
            double y = (Math.random() * 2 - 1) * size;
            double z = (Math.random() * 2 - 1) * size;

            Location loc = origin.clone().add(x, y, z);
            sendParticlePacket(receivers, particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, data);
        }
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

                    if (player.getLocation().distanceSquared(origin) > 100) continue;

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

    public static void spawnCloudParticlesToAll(Location center, Particle particle, int count, double radiusCloud, double height, Collection<Player> receivers) {
        for (Player player : receivers) {
            spawnCloudParticles(player, particle, center, count, radiusCloud, height);
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

    public static <T> void spawnConvergingParticlesSpherical(Location target, Particle particle, int count, double radius, double radiusPlayer, int durationTicks, T data) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Location> startPoints = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);

            startPoints.add(target.clone().add(x, y, z));
        }

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > durationTicks) {
                    cancel();
                    return;
                }

                Collection<Player> receivers = target.getNearbyEntitiesByType(Player.class, radiusPlayer);
                if (!receivers.isEmpty()) {
                    double progress = (double) tick / durationTicks;

                    for (Location start : startPoints) {
                        double x = start.getX() + (target.getX() - start.getX()) * progress;
                        double y = start.getY() + (target.getY() - start.getY()) * progress;
                        double z = start.getZ() + (target.getZ() - start.getZ()) * progress;

                        Location point = new Location(target.getWorld(), x, y, z);

                        sendParticlePacket(receivers, particle, point, 1, 0.0, 0.0, 0.0, 0.0, data);
                    }
                }

                tick++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 1L);
    }

    public static <T> void spawnRepulsedParticlesSpherical(Location target, Particle particle, int count, double radius, double radiusPlayer, int durationTicks, T data) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Location> endPoints = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);

            endPoints.add(target.clone().add(x, y, z));
        }

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > durationTicks) {
                    cancel();
                    return;
                }

                Collection<Player> receivers = target.getNearbyEntitiesByType(Player.class, radiusPlayer);
                if (!receivers.isEmpty()) {
                    double progress = (double) tick / durationTicks;

                    for (Location end : endPoints) {
                        double x = target.getX() + (end.getX() - target.getX()) * progress;
                        double y = target.getY() + (end.getY() - target.getY()) * progress;
                        double z = target.getZ() + (end.getZ() - target.getZ()) * progress;

                        Location point = new Location(target.getWorld(), x, y, z);


                        sendParticlePacket(receivers, particle, point, 1, 0.0, 0.0, 0.0, 0.0, data);
                    }
                }

                tick++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 1L);
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

    public static <T> void spawnCubeParticles(Location center, Particle particle, double sizeX,
                                              double sizeY, double sizeZ, int count, double radius,
                                              T data) {
        World world = center.getWorld();
        if (world == null) return;

        Collection<Player> receivers = center.getNearbyEntitiesByType(Player.class, radius);
        if (receivers.isEmpty()) return;

        for (int i = 0; i < count; i++) {
            double x = (Math.random() * 2 - 1) * sizeX;
            double y = (Math.random() * 2 - 1) * sizeY;
            double z = (Math.random() * 2 - 1) * sizeZ;

            Location particleLoc = center.clone().add(x, y, z);

            for (Player player : receivers) {
                sendParticlePacket(player, particle, particleLoc, 1, 0.0, 0.0, 0.0, 0.0, data);
            }
        }
    }
}
