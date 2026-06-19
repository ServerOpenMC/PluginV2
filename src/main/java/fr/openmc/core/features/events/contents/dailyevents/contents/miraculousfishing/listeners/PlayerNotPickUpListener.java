package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.listeners;

import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerNotPickUpListener implements Listener {
    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!event.getItem().getItemStack().getPersistentDataContainer().has(MiraculousFishingManager.NOT_PICKUP_KEY)) return;

        event.getItem().remove();
        event.setCancelled(true);
    }
}
