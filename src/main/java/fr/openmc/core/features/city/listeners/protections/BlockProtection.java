package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        ProtectionsManager.checkPermissions(event.getPlayer(), event, CityPermission.PLACE, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
       ProtectionsManager.checkPermissions(event.getPlayer(), event, CityPermission.BREAK, false);
    }
}
