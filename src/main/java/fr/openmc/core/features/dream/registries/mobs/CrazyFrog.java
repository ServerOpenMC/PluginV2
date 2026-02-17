package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLoot;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class CrazyFrog extends DreamMob {

    public CrazyFrog() {
        super("crazy_frog",
                "Grenouille Folle",
                EntityType.FROG,
                18.0,
                0L,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(3, 2.3),
                List.of(new DreamLoot(
                        DreamItemRegistry.getByName("metal_detector"),
                        0.5,
                        1,
                        1
                ))
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        Frog frog = (Frog) this.getPreBuildMob(location);

        frog.setVariant(Frog.Variant.WARM);
        frog.setVelocity(location.getDirection().multiply(1.3));

        return frog;
    }
}
