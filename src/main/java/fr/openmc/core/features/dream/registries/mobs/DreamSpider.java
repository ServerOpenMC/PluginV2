package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Spider;

import java.util.List;

public class DreamSpider extends DreamMob<Spider> {

    public DreamSpider(String id) {
        super(id,
                TranslationManager.translationString("feature.dream.mob.dream_spider"),
                Spider.class,
                8.0,
                1L,
                RandomUtils.randomBetween(0.2, 0.3),
                RandomUtils.randomBetween(1.5, 2.0),
                List.of(new ItemLoot(
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