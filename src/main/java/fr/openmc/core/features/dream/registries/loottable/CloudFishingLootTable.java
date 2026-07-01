package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;

import java.util.Set;

public class CloudFishingLootTable extends CustomLootTable {
    @Override
    public String getNamespace() { return "omc_dream:cloud_fishing"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.METEO_WAND,
                        0.05,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.POISSONION,
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.MOON_FISH,
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.SUN_FISH,
                        0.5,
                        1,
                        2
                ),
                new CustomLoot(
                        DreamItemRegistry.DOCKER_FISH,
                        0.1,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.SOMNIFERE,
                        0.4,
                        1,
                        1
                )
        );
    }
}
