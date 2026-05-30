package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Tadpole;
import org.bukkit.event.entity.EntityDeathEvent;

public class CorruptedTadpole extends DreamMob<Tadpole> {

    public CorruptedTadpole(String id) {
        super(id,
                "Tétard Corrompu",
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
        CustomMob<?> crazyFrog = OMCRegistry.CUSTOM_MOBS.getMob("omc_dream:crazy_frog");
        if (crazyFrog == null) return;
        crazyFrog.spawn(event.getEntity().getLocation());
    }
}