package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ContestParticlesUtils {


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
                if (WeeklyEventsManager.getCurrentEvent() != null) return;

                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location particleLocation = new Location(world, x, y, z);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.getWorld().equals(world)) continue;

                        if (!region.contains(BukkitAdapter.asBlockVector(player.getLocation()))) continue;

                        ParticleUtils.sendParticlePacket(player, particle, particleLocation);
                    }
                }
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
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
                    ParticleUtils.spawnRisingDustParticle(regionId, world, base, color1, 1.0f, 15, 1);
                }

                for (int i = 0; i < amountPer2Tick; i++) {
                    double x = RandomUtils.randomBetween(minLocation.getX(), maxLocation.getX());
                    double y = RandomUtils.randomBetween(minLocation.getY(), maxLocation.getY());
                    double z = RandomUtils.randomBetween(minLocation.getZ(), maxLocation.getZ());

                    Location base = new Location(world, x, y, z);
                    ParticleUtils.spawnRisingDustParticle(regionId, world, base, color2, 1.0f, 15, 1);
                }
            }
        }.runTaskTimerAsynchronously(OMCPlugin.getInstance(), 0L, 2L);
    }
}
