package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.entity.Creaking;

public class DreamCreaking extends DreamMob<Creaking> {

    public DreamCreaking(String id) {
        super(id,
                "Creaking Insomiaque",
                Creaking.class,
                1,
                2L,
                RandomUtils.randomBetween(0.4, 0.6),
                RandomUtils.randomBetween(1.2, 1.7)
        );
    }
}