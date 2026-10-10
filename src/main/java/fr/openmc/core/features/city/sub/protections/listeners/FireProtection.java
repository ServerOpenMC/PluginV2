package fr.openmc.core.features.city.sub.protections.listeners;


import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class FireProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public FireProtection() {
        this.protectionsManager = OMCRegistry.CITY_FEATURES.PROTECTIONS;
    }
    @EventHandler(ignoreCancelled = true)
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player == null) return;
        
        protectionsManager.verify(event.getPlayer(), event, loc);
    }
}
