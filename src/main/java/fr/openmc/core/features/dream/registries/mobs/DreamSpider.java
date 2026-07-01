package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Spider;

import java.util.List;

public class DreamSpider extends DreamMob<Spider> {

    public DreamSpider(String id) {
        super(id,
                "Arraignée Infestée",
                Spider.class,
                8.0,
                1L,
                RandomUtils.randomBetween(0.2, 0.3),
                RandomUtils.randomBetween(1.5, 2.0),
                List.of(new CustomLoot(
                        DreamItemRegistry.CORRUPTED_STRING,
                        0.80,
                        1,
                        3
                ))
        );
    }

    @Override
    public Spider spawn(Location location) {
        return this.getPreBuildMob(location);
    }
}