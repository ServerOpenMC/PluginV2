package fr.openmc.core.utils;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class WorldGuardApi {

    private static boolean hasWorldGuard;

    public WorldGuardApi() {
        hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public static boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    public static boolean isRegionConflict(Player player, Location location) {

        if(!hasWorldGuard()) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(player.getWorld().getName());
        RegionManager regions = container.get(world);

        if(regions == null) return false;

        for(ProtectedRegion region : regions.getRegions().values()) {
            if(isInside(region, location)) return true;
        }

        return false;
    }

    public static boolean isInside(ProtectedRegion region, Location location) {
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean doesChunkContainWGRegion(Chunk chunk) {
        if (!hasWorldGuard()) return false;

        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Location location = new Location(chunk.getWorld(), x, 0, z);
                if (isRegionConflict(location)) {
                    return true;
                }
            }
        }
        return false;
    }
}
