package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Tadpole;
import org.bukkit.event.entity.EntityDeathEvent;

public class CorruptedTadpole extends DreamMob<Tadpole> {

    public CorruptedTadpole(String id) {
        super(id,
                TranslationManager.translation("feature.dream.mob.corrupted_tadpole"),
                Tadpole.class,
                25.0,
                0L,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(5, 6.3)
        );
    }

    @Override
    public Tadpole spawn(Location location) {
        return this.getPreBuildMob(location);
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        CustomMob<?> crazyFrog = DreamMobsRegistry.CRAZY_FROG.getMob();
        if (crazyFrog == null) return;
        crazyFrog.spawn(event.getEntity().getLocation());
    }
}