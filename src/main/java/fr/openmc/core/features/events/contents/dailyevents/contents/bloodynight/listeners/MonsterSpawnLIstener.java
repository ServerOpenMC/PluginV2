package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.listeners;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightManager;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MonsterSpawnLIstener implements Listener {
    @EventHandler
    public void onMonsterSpawn(EntitySpawnEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()
                || !(DailyEventsManager.getActiveDailyEvent() instanceof BloodyNightEvent)) return;
        if (!(event.getEntity() instanceof Monster monster)) return;

        ParticleUtils.spawnDispersingParticles(event.getLocation(),
                Particle.DAMAGE_INDICATOR,
                10, 35, 0.1D, null);

        BloodyNightManager.boostMonster(monster);
    }
}
