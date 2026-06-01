package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Frog;

import java.util.List;

public class CrazyFrog extends DreamMob<Frog> {

    public CrazyFrog(String id) {
        super(id,
                "Grenouille Folle",
                Frog.class,
                18.0,
                0L,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(3, 2.3),
                List.of(new CustomLoot(
                        DreamItemRegistry.METAL_DETECTOR,
                        0.5,
                        1,
                        1
                ))
        );
    }

    @Override
    public Frog spawn(Location location) {
        Frog frog = this.getPreBuildMob(location);

        frog.setVariant(Frog.Variant.WARM);
        frog.setVelocity(location.getDirection().multiply(1.3));

        return frog;
    }
}
