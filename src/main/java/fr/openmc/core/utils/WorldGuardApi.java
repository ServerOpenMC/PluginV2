package fr.openmc.core.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Collections;

public class WorldGuardApi {

    private static boolean hasWorldGuard;

    public WorldGuardApi() {
        hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public static boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    public static boolean isRegionConflict(Location location) {
        if(!hasWorldGuard()) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(location.getWorld().getName());
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

        org.bukkit.World world = chunk.getWorld();
        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) return false;

        Collection<ProtectedRegion> chunkRegion = Collections.singleton(new ProtectedCuboidRegion(
                "__temp_check__",
                BlockVector3.at(minX, minY, minZ),
                BlockVector3.at(maxX, maxY, maxZ)
        ));

        for (ProtectedRegion region : regions.getRegions().values()) {
            if (!region.getIntersectingRegions(chunkRegion).isEmpty() || region.getIntersectingRegions(chunkRegion).contains(region)) {
                return true;
            }
        }

        return false;
    }
}
