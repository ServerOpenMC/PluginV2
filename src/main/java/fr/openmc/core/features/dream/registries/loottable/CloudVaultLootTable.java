package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;
import net.kyori.adventure.key.Key;

import java.util.Set;

public class CloudVaultLootTable extends CustomLootTable {
    @Override
    public String getName() { return "omc_dream:cloud_vault"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.getByName("cloud_helmet"),
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("cloud_chestplate"),
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("cloud_leggings"),
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("cloud_boots"),
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("somnifere"),
                        0.45,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.getByName("cloud_fishing_rod"),
                        0.08,
                        1,
                        1
                ),
                new CustomLoot(
                        CustomEnchantmentRegistry.getCustomEnchantmentByKey(
                                Key.key("omc_dream:dream_sleeper")
                        ).getEnchantedBookItem(2).getBest(),
                        0.10,
                        1,
                        1
                )
        );
    }
}
