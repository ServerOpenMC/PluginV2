package fr.openmc.core.features.city.listeners.protections;


import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class FireProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public FireProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }
    @EventHandler(ignoreCancelled = true)
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (player == null) return;
        
        protectionsManager.verify(event.getPlayer(), event, loc);
    }
}
