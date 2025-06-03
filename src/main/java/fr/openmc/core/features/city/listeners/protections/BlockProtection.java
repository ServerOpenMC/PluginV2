package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (ProtectionsManager.checkClaimAndCheckIfIsMember(event.getPlayer(), event, event.getBlock().getLocation())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, event.getBlock().getLocation(), CPermission.PLACE);
        }
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        if (ProtectionsManager.checkClaimAndCheckIfIsMember(event.getPlayer(), event, event.getBlock().getLocation())) {
            ProtectionsManager.checkPermissions(event.getPlayer(), event, event.getBlock().getLocation(), CPermission.BREAK);
        }
    }
}
