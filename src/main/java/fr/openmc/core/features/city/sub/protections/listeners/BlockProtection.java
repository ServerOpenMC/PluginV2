package fr.openmc.core.features.city.sub.protections.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public BlockProtection() {
        this.protectionsManager = OMCRegistry.CITY_FEATURES.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        City city = City.of(event.getBlock());
        if (city == null) return;
      
        if (city.isMember(event.getPlayer())) {
            protectionsManager.checkPermissions(event.getPlayer(), event, city, CityPermission.PLACE);
        } else {
            protectionsManager.checkCity(event.getPlayer(), event, city, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onBlockBreak(BlockBreakEvent event) {
        City city = City.of(event.getBlock());
        if (city == null) return;
        
        if (city.isMember(event.getPlayer())) {
            protectionsManager.checkPermissions(event.getPlayer(), event, city, CityPermission.BREAK);
        } else {
            protectionsManager.checkCity(event.getPlayer(), event, city, false);
        }
    }
}
