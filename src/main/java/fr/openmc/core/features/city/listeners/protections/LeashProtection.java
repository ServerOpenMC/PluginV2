package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class LeashProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public LeashProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    void onLeash(PlayerLeashEntityEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    void onUnleash(PlayerUnleashEntityEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getEntity().getLocation());
    }

}
