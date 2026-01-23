package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class DreamMobDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!DreamMobsRegistry.isDreamMob(livingEntity)) return;

        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            Entity damager = entityEvent.getDamager();

            if (damager instanceof Player) return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (!(damaged instanceof Player p)) return;
        if (!(damager instanceof LivingEntity livingEntity)) return;
        if (!DreamMobsRegistry.isDreamMob(livingEntity)) return;

        DreamMob dreamMob = DreamMobsRegistry.getFromEntity(livingEntity);
        if (dreamMob == null) return;

        event.setCancelled(true);

        Vector kb = p.getLocation().toVector()
                .subtract(livingEntity.getLocation().toVector())
                .normalize()
                .multiply(0.9)
                .setY(0.4);

        p.setVelocity(kb);
        p.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
        DreamUtils.removeDreamTime(p, dreamMob.getDamageTime(), true);
    }

}
