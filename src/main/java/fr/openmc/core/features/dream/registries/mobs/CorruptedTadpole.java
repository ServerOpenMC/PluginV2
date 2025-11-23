package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class CorruptedTadpole extends DreamMob implements Listener {

    public CorruptedTadpole() {
        super("corrupted_tadpole",
                "Tétard Corrompu",
                EntityType.TADPOLE,
                25.0,
                0L,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(5, 6.3),
                List.of()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return this.getPreBuildMob(location);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!DreamMobsRegistry.isDreamMob(entity)) return;

        DreamMob mob = DreamMobsRegistry.getFromEntity(entity);
        if (mob == null) return;
        if (!mob.getId().equals(this.getId())) return;

        DreamMob crazyFrog = DreamMobsRegistry.getByName("crazy_frog");

        if (crazyFrog == null) return;
        crazyFrog.spawn(entity.getLocation());
    }
}