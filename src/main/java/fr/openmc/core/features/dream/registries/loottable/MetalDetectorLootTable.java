package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import net.kyori.adventure.key.Key;

import java.util.Set;

public class MetalDetectorLootTable extends CustomLootTable {
    @Override
    public String getName() {return "omc_dream:metal_detector";}

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_dihydrogene"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_jimmy"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_terre"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_sans_plomb"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_nature"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_aywen"),
                        0.3,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("chips_lait_2_margouta"),
                        0.2,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("somnifere"),
                        0.4,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("mud_orb"),
                        0.1,
                        1,
                        1
                ),
                new CustomLoot(
                        (DreamItem) CustomEnchantmentRegistry.getCustomEnchantmentByKey(Key.key("omc_dream:experientastic")).getEnchantedBookItem(1),
                        0.1,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("crystallized_pickaxe"),
                        0.1,
                        1,
                        1
                )
        );
    }
}
