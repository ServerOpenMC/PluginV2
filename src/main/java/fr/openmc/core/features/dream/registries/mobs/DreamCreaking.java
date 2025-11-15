package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class DreamCreaking extends DreamMob {

    public DreamCreaking() {
        super("dream_creaking",
                "Creaking Insomiaque",
                EntityType.CREAKING,
                1,
                2L,
                RandomUtils.randomBetween(0.4, 0.6),
                RandomUtils.randomBetween(1.2, 1.7),
                null
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public void apply(Creaking creaking) {
        applyStats(creaking);
    }
}