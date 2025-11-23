package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.models.registry.loottable.DreamLoot;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import net.kyori.adventure.key.Key;

import java.util.Set;

public class CloudFishingLootTable extends DreamLootTable {
    @Override
    public Key getKey() {
        return Key.key("dream:cloud_fishing");
    }

    @Override
    public Set<DreamLoot> getLoots() {
        return Set.of(
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:meteo_wand"),
                        0.1,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:poissonion"),
                        0.5,
                        1,
                        2
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:moon_fish"),
                        0.5,
                        1,
                        2
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:sun_fish"),
                        0.5,
                        1,
                        2
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:dockerfish"),
                        0.2,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:somnifere"),
                        0.4,
                        1,
                        1
                )
        );
    }
}
