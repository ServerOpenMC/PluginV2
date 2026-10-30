package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;

import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

// dupli
public class LeashProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    void onLeash(PlayerLeashEntityEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    void onUnleash(PlayerUnleashEntityEvent event) {
        if (!event.getPlayer().getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

}
