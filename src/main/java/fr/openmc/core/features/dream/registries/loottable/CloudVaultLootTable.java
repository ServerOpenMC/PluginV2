package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.loottable.CustomLootTable;

import java.util.Set;

public class CloudVaultLootTable extends CustomLootTable {
    @Override
    public String getNamespace() { return "omc_dream:cloud_vault"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new CustomLoot(
                        DreamItemRegistry.CLOUD_HELMET,
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CLOUD_CHESTPLATE,
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CLOUD_LEGGINGS,
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CLOUD_BOOTS,
                        0.125,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.SOMNIFERE,
                        0.45,
                        1,
                        1
                ),
                new CustomLoot(
                        DreamItemRegistry.CLOUD_FISHING_ROD,
                        0.08,
                        1,
                        1
                ),
                new CustomLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.DREAM_SLEEPER.getEnchantedBookItem(2).getBest(),
                        0.10,
                        1,
                        1
                )
        );
    }
}
