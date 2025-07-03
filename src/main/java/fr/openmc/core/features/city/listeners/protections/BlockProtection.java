package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockProtection implements Listener {
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) return; // used in city.sub.war.listener.TntPlaceListener.java

        ProtectionsManager.verify(event.getPlayer(), event, event.getBlock().getLocation());
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getBlock().getLocation());
    }
}
