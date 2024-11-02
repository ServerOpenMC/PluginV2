package fr.openmc.core.features.city;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

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
}
