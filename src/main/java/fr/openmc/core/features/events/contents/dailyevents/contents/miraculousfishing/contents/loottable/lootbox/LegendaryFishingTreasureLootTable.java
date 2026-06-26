package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LegendaryFishingTreasureLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.LEGENDARY_FISHING_TREASURE.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_daily_events:legendary_fishing_treasure";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_HELMET, 0.10, 1, 1),
                new ItemLoot(OMCRegistry.CUSTOM_ITEMS.ANCIENT_FISHER_BOOTS, 0.10, 1, 1),
                new ItemLoot(DreamItemRegistry.EWENITE_BLOCK, 0.006, 1, 1)
        ));
    }
}