package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import net.kyori.adventure.key.Key;

import java.util.Set;

public class CloudFishingLootTable extends CustomLootTable {
    @Override
    public String getName() { return "omc_dream:cloud_fishing"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.getByName("meteo_wand"),
                        0.1,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("poissonion"),
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("moon_fish"),
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("sun_fish"),
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("dockerfish"),
                        0.2,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("somnifere"),
                        0.4,
                        1,
                        1
                )
        );
    }
}
