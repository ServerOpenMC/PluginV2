package fr.openmc.core.features.city.sub.protections.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public FishProtection() {
        this.protectionsManager = OMCRegistry.CITY_FEATURES.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    void onFish(PlayerFishEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getHook().getLocation());
    }
}
