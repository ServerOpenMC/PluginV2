package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class LeashProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onLeash(PlayerLeashEntityEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    void onUnleash(PlayerUnleashEntityEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

}
