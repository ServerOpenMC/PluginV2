package fr.openmc.core.utils;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardApi {

    public static boolean isRegionConflict(Player player, Location location) {

        Plugin wg = player.getServer().getPluginManager().getPlugin("WorldGuard");
        if(wg == null || !wg.isEnabled()) return false;

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

}
