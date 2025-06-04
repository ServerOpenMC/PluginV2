package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class LeashProtection implements Listener {
    @EventHandler
    void onLeash(PlayerLeashEntityEvent event) {
        ProtectionsManager.checkClaim(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler
    void onUnleash(PlayerUnleashEntityEvent event) {
        ProtectionsManager.checkClaim(event.getPlayer(), event, event.getEntity().getLocation());
    }

}
