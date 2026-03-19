package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (DreamUtils.isInDream(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (victim instanceof Player victimPlayer && DreamUtils.isInDreamWorld(victimPlayer)
                && damager instanceof Player damagerPlayer && DreamUtils.isInDreamWorld(damagerPlayer)) {
            event.setCancelled(true);
        }
    }
}
