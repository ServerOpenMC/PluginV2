package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.UUID;

public class PlayerPickupListener implements Listener {

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!DreamUtils.isInDreamWorld(player)) return;

        Item item = event.getItem();

        UUID thrower = item.getThrower();

        if (thrower != null && !thrower.equals(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
