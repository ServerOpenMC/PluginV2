package fr.openmc.core.registry.mobs.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobRegistry;
import fr.openmc.core.registry.mobs.options.MobBossbarImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomMobLoadListener implements Listener {
    @EventHandler
    public void onEntityLoad(EntityAddToWorldEvent event) {
        CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(event.getEntity());
        if (customMob == null) return;
        if (!(customMob instanceof MobBossbarImpl)) return;
        if (!(event.getEntity() instanceof MobBossbarImpl)) return;

        CustomMobRegistry.HAS_BOSSBAR.add(event.getEntity().getUniqueId());
    }
}
