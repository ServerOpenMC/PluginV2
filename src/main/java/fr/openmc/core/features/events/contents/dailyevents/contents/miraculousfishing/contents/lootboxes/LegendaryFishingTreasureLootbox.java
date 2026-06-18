package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.stream.IntStream;

public class LegendaryFishingTreasureLootbox extends CustomLootbox {
    public LegendaryFishingTreasureLootbox() {
        super(
                "omc_daily_events:legendary_fishing_treasure",
                TranslationManager.translation("feature.dailyevents.miraculousfishing.lootbox.legendary_fishing_treasure.name"),
                OMCRegistry.CUSTOM_LOOT_TABLES.LEGENDARY_FISHING_TREASURE,
                new LootboxOptions(
                        InventorySize.NORMAL,
                        60,
                        IntStream.range(10, 17).boxed().toList(),
                        13
                )
        );
    }
}

