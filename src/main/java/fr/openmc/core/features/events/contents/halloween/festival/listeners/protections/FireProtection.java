package fr.openmc.core.features.events.contents.halloween.festival.listeners.protections;


import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

// dupli
public class FireProtection implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onFireIgnite(BlockIgniteEvent event) {
        Location loc = event.getBlock().getLocation();

        if (!loc.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        event.setCancelled(true);
    }
}
