package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        City city = CityManager.getCityFromChunk(event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (city == null) return;
        
        if (city.isMember(event.getPlayer())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, city, CPermission.PLACE);
        } else {
            ProtectionsManager.checkCity(event.getPlayer(), event, city);
        }
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        City city = CityManager.getCityFromChunk(event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (city == null) return;
        
        if (city.isMember(event.getPlayer())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, city, CPermission.BREAK);
        } else {
            ProtectionsManager.checkCity(event.getPlayer(), event, city);
        }
    }
}
