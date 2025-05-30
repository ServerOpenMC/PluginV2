package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlaceBlock(BlockPlaceEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getBlock().getLocation());
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getBlock().getLocation());
    }
}
