package fr.openmc.core.features.city.listeners.protections;


import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class FireProtection implements Listener {
    @EventHandler
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player == null) return;
        
        ProtectionsManager.verify(event.getPlayer(), event, loc);
    }
}
