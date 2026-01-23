package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.models.registry.loottable.DreamLoot;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.features.dream.registries.DreamEnchantementRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import net.kyori.adventure.key.Key;

import java.util.Set;

public class MetalDetectorLootTable extends DreamLootTable {
    @Override
    public Key getKey() {
        return Key.key("dream:metal_detector");
    }

    @Override
    public Set<DreamLoot> getLoots() {
        return Set.of(
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_dihydrogene"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_jimmy"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_terre"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_sans_plomb"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_nature"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_aywen"),
                        0.3,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:chips_lait_2_margouta"),
                        0.2,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:somnifere"),
                        0.4,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:mud_orb"),
                        0.1,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamEnchantementRegistry.getDreamEnchantment(Key.key("dream:experientastic")).getEnchantedBookItem(1),
                        0.1,
                        1,
                        1
                ),
                new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:crystalized_pickaxe"),
                        0.1,
                        1,
                        1
                )
        );
    }
}
