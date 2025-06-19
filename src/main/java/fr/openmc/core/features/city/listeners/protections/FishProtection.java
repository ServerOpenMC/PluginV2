package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.ProtectionsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishProtection implements Listener {
    @EventHandler
    void onFish(PlayerFishEvent event) {
        ProtectionsManager.verify(event.getPlayer(), event, event.getHook().getLocation());
    }
}
