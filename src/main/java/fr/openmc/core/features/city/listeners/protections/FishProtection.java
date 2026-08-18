package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishProtection implements Listener {
    private final ProtectionsManager protectionsManager;

    public FishProtection(CityManager cityManager) {
        this.protectionsManager = cityManager.PROTECTIONS;
    }

    @EventHandler(ignoreCancelled = true)
    void onFish(PlayerFishEvent event) {
        protectionsManager.verify(event.getPlayer(), event, event.getHook().getLocation());
    }
}
