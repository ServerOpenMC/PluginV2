package fr.openmc.core.features.city.listeners.protections;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class FoodProtection implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodConsume(PlayerItemConsumeEvent event) {
        // on laisse les gens manger
    }
}
