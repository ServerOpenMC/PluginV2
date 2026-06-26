package fr.openmc.core.features.dream.registries.loottable;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CloudVaultLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dream.loot_table.cloud_vault");
    }

    @Override
    public String getNamespace() { return "omc_dream:cloud_vault"; }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        DreamItemRegistry.CLOUD_HELMET,
                        0.125,
                        1,
                        1
                ),
                new ItemLoot(
                        DreamItemRegistry.CLOUD_CHESTPLATE,
                        0.125,
                        1,
                        1
                ),
                new ItemLoot(
                        DreamItemRegistry.CLOUD_LEGGINGS,
                        0.125,
                        1,
                        1
                ),
                new ItemLoot(
                        DreamItemRegistry.CLOUD_BOOTS,
                        0.125,
                        1,
                        1
                ),
                new ItemLoot(
                        DreamItemRegistry.SOMNIFERE,
                        0.45,
                        1,
                        1
                ),
                new ItemLoot(
                        DreamItemRegistry.CLOUD_FISHING_ROD,
                        0.08,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.DREAM_SLEEPER.getEnchantedBookItem(2).getBest(),
                        0.10,
                        1,
                        1
                )
        ));
    }
}
