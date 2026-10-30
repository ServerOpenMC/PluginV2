package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

// dupli
public class FishProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onFish(PlayerFishEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }
}
