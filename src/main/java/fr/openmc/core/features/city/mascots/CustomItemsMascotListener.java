package fr.openmc.core.features.city.mascots;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class CustomItemsMascotListener implements Listener {
    @EventHandler
    public void onBlockPlace(CustomBlockPlaceEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5);

        for (Entity entity : nearbyEntities) {
            if (MascotUtils.isMascot(entity)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
