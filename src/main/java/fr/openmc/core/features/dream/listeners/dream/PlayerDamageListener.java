package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import org.bukkit.entity.Entity;
import org.bukkit.Sound;
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
            double fallDistance = player.getFallDistance();
            if (fallDistance < 5) return;

            long secondsLost = (long) (fallDistance * 1.5);

            DreamUtils.removeDreamTime(player, secondsLost, true);
            player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1f, 1f);

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (victim instanceof Player victimPlayer && DreamUtils.isInDreamWorld(victimPlayer)
                && damager instanceof Player damagerPlayer && DreamUtils.isInDreamWorld(damagerPlayer)) {
            event.setCancelled(true);
        }
    }
}
