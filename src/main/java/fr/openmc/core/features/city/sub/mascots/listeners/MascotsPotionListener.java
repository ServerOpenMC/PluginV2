package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public class MascotsPotionListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            handleMascotProtection(event, affectedEntity);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAreaEffectCloud(AreaEffectCloudApplyEvent event) {
        for (LivingEntity affectedEntity : event.getAffectedEntities()) {
            handleMascotProtection(event, affectedEntity);
        }
    }

    private void handleMascotProtection(Cancellable event, LivingEntity affectedEntity) {
        if (!MascotUtils.canBeAMascot(affectedEntity)) return;

        City cityMascot = MascotUtils.getCityFromEntity(affectedEntity.getUniqueId());
        if (cityMascot == null) return;

        if (!cityMascot.isInWar()) return;

        event.setCancelled(true);
    }
}
