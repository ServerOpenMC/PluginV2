package fr.openmc.core.registry.mobs.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.options.MobBossbarImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class CustomMobBossbarListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onCustomMobDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);

        if (!(customMob instanceof MobBossbarImpl bossBarMob)) return;

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!entity.isValid() || entity.isDead()) {
                bossBarMob.removeBossBar(entity);
                return;
            }

            bossBarMob.updateBossBar(entity);
            bossBarMob.updateBossBarViewers(entity);
        });
    }

    @EventHandler
    public void onCustomMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);

        if (customMob instanceof MobBossbarImpl bossBarMob) {
            bossBarMob.removeBossBar(entity);
        }
    }
}
