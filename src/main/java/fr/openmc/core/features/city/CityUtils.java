package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class CityUtils {
    public static boolean doesRegionOverlap(World world, Location minLocation, Location maxLocation) {
        // Obtenir le conteneur de régions pour le monde donné
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return false; // Pas de régions dans ce monde
        }

        for (var entry : regionManager.getRegions().entrySet()) {
            ProtectedRegion region = entry.getValue();

            if (region.contains(minLocation.getBlockX(), minLocation.getBlockY(), minLocation.getBlockZ()) || region.contains(maxLocation.getBlockX(), maxLocation.getBlockY(), maxLocation.getBlockZ())) {
                return true;
            }
        }
        return false;
    }

    public static double getPolygonalRegionArea(ProtectedRegion region) {
        ProtectedPolygonalRegion polygonRegion = (ProtectedPolygonalRegion) region;
        List<BlockVector2> points = polygonRegion.getPoints();

        return calculatePolygonArea(points);
    }

    private static double calculatePolygonArea(List<BlockVector2> points) {
        double area = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            BlockVector2 p1 = points.get(i);
            BlockVector2 p2 = points.get((i + 1) % n); // Wrap around to the first point

            area += p1.x() * p2.z() - p2.x() * p1.z();
        }

        area = Math.abs(area) / 2.0;
        return area;
    }
}
