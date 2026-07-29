package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        City city = CityManager.getCityFromChunk(event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (city == null) return;
      
        if (city.isMember(event.getPlayer())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, city, CityPermission.PLACE);
        } else {
            ProtectionsManager.checkCity(event.getPlayer(), event, city, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onBlockBreak(BlockBreakEvent event) {
        City city = CityManager.getCityFromChunk(event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (city == null) return;
        
        if (city.isMember(event.getPlayer())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, city, CityPermission.BREAK);
        } else {
            ProtectionsManager.checkCity(event.getPlayer(), event, city, false);
        }
    }
}
