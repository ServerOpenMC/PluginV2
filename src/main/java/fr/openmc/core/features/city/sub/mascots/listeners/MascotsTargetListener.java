package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class MascotsTargetListener implements Listener {

    @EventHandler
    public void onMobTargetMascot(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();

        if (target == null) return;
        if (!MascotUtils.isMascot(target)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onMascotTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();

        if (MascotUtils.isMascot(entity)) {
            event.setCancelled(true);
        }
    }
}
