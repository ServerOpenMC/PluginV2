package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.contents.loottables;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CropBreakLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return TranslationManager.translation("feature.dailyevents.golden_harvest.loot_table.crop_break");
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:crop_break";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.PLANTATION.getEnchantedBookItem(1),
                        0.01,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ENCHANTS.PLANTATION.getEnchantedBookItem(2),
                        0.005,
                        1
                )
        ));
    }
}