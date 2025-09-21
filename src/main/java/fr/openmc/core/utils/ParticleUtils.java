package fr.openmc.core.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.contest.managers.ContestManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtils {

    public static Color color1;
    public static Color color2;

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
                if (ContestManager.data.getPhase() == 3) return;
                List<ServerPlayer> players = Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(player ->
                                player.getWorld().equals(world) &&
                                        region.contains(BukkitAdapter.asBlockVector(player.getLocation())))
                        .map(player -> ((CraftPlayer) player).getHandle())
                        .toList();
                if (players.isEmpty()) return;
                List<Packet<? super ClientGamePacketListener>> particlePackets = new ArrayList<>();
                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location particleLocation = new Location(world, x, y, z);

                    particlePackets.add(createParticlePacket(particle, particleLocation));
                }

                ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(particlePackets);
                players.forEach(player -> player.connection.send(bundlePacket));
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
    }

    public static ClientboundLevelParticlesPacket createParticlePacket(Particle particle, Location loc) {
        return createParticlePacket(particle, loc, 3, 0.2f, 0.2f, 0.2f, 0.01f, null);
    }

    public static <T> ClientboundLevelParticlesPacket createParticlePacket(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double speed, T data) {
        return new ClientboundLevelParticlesPacket(
                CraftParticle.createParticleParam(particle, data),
                false,
                false,
                location.x(), location.y(), location.z(),
                (float) offsetX, (float) offsetY, (float) offsetZ,
                (float) speed,
                count
        );
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
                if (ContestManager.data.getPhase() != 3) return;

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

                List<RisingDustParticle> particles = new ArrayList<>();
                int rgbColor1 = color1.asRGB();
                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location base = new Location(world, x, y, z);
                    particles.add(new RisingDustParticle(base, rgbColor1));
                }

                int rgbColor2 = color2.asRGB();
                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location base = new Location(world, x, y, z);
                    particles.add(new RisingDustParticle(base, rgbColor2));
                }
                spawnRisingDustParticles(regionId, world, particles, 1.0f, 15, 1);
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
    }

    public static void spawnRisingDustParticles(String regionId, World world, List<RisingDustParticle> particles, float size, int steps, int count) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null) return;

        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) return;

        new BukkitRunnable() {
            int stepCount = 0;
            @Override
            public void run() {
                if (stepCount > steps) {
                    cancel();
                    return;
                }
                List<Packet<? super ClientGamePacketListener>> particlePackets = new ArrayList<>();
                for (RisingDustParticle particle : particles) {
                    Vec3 position = new Vec3(particle.origin.getX(), particle.origin.getY() + (stepCount * 0.10), particle.origin.getZ());
                    ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                            new DustParticleOptions(particle.color, size), true, true,
                            position.x, position.y, position.z,
                            0, 0.1f, 0, 0.01f, count
                    );
                    particlePackets.add(packet);
                }
                if (!particlePackets.isEmpty()) {
                    ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(particlePackets);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.getWorld().equals(world)) continue;
                        if (!region.contains(BukkitAdapter.asBlockVector(player.getLocation()))) continue;
                        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                        nmsPlayer.connection.send(bundlePacket);
                    }
                }
                stepCount++;
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 1L);
    }

    public record RisingDustParticle(Location origin, int color) {}
}
