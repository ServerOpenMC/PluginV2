package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireBoss;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs.vampire.VampireSlave;
import fr.openmc.core.registry.mobs.CustomMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MonsterSpawnListener implements Listener {
    @EventHandler
    public void onNormalMonsterSpawn(EntitySpawnEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent bloodyEvent)) return;
        if (!event.getLocation().getWorld().getName().equals(bloodyEvent.getWorldEvent())) return;

        if (!(event.getEntity() instanceof Monster monster)) return;
        if (monster.getPersistentDataContainer().has(BloodyNightManager.RAID_MONSTER_KEY)) {
            BloodyNightManager.applyBloodyMonster(monster);
        } else {
            OMCRegistry.CUSTOM_MOBS.CORRUPTED_MONSTER.apply(monster);
        }
    }

    @EventHandler
    public void onMonsterLoaded(EntityAddToWorldEvent event) {
        if (DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent) return;

        if (!(event.getEntity() instanceof Monster monster)) return;

        if (monster.getPersistentDataContainer().has(BloodyNightManager.RAID_MONSTER_KEY)) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                if (monster.isValid()) {
                    monster.remove();
                } else {
                    OMCLogger.error("Impossible de supprimer le monstre (mort, delete, non chargé) " + monster.getName());
                }
            });
            return;
        }

        BloodyNightManager.desactivateCorruptedMonster(monster);
    }

    @EventHandler
    public void onVampireLoaded(EntityAddToWorldEvent event) {
        if (DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent) return;

        Entity entity = event.getEntity();

        CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (customMob == null) return;
        if (customMob instanceof VampireBoss bossbar) {
            bossbar.removeBossBar(livingEntity);
            livingEntity.remove();
        } else if (customMob instanceof VampireSlave)
            livingEntity.remove();
    }
}
