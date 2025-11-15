package fr.openmc.core.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationUtils {
    public static Location getSafeNearbySurface(Location location, int radius) {
        World world = location.getWorld();
        int baseY = location.getBlockY();
        int maxY = Math.min(baseY + 10, world.getMaxHeight() - 2);
        int minY = Math.max(baseY - 10, world.getMinHeight() + 1);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int baseX = location.getBlockX() + dx;
                int baseZ = location.getBlockZ() + dz;

                for (int dy = 0; dy <= (maxY - minY); dy++) {
                    int offsetY = (dy % 2 == 0) ? dy / 2 : -(dy / 2);
                    int y = baseY + offsetY;
                    if (y > maxY || y < minY) continue;

                    Block under = world.getBlockAt(baseX, y - 1, baseZ);
                    Block feet = world.getBlockAt(baseX, y, baseZ);
                    Block head = world.getBlockAt(baseX, y + 1, baseZ);

                    if (!under.isPassable() && feet.isPassable() && head.isPassable()) {
                        return new Location(world, baseX + 0.5, y, baseZ + 0.5);
                    }
                }
            }
        }

        return new Location(
                world,
                location.getBlockX() + 0.5,
                location.getBlockY(),
                location.getBlockZ() + 0.5
        );
    }

    public static Location findLocationInBiome(World world, Biome biome) {
        Random random = new Random();
        WorldBorder border = world.getWorldBorder();

        Location center = border.getCenter();
        double radius = border.getSize() / 2.0;

        for (int i = 0; i < 1000; i++) {
            double x = center.getX() + (random.nextDouble() * 2 - 1) * radius;
            double z = center.getZ() + (random.nextDouble() * 2 - 1) * radius;

            Location loc = world.getHighestBlockAt((int) x, (int) z).getLocation();

            if (world.getBiome(loc) == biome) {
                return loc.add(0, 1, 0);
            }
        }
        return null;
    }

    public static boolean isSameLocation(Location a, Location b) {
        if (a == null || b == null) return false;
        return a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
