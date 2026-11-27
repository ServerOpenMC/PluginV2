package fr.openmc.core.features.events.contents.christmas.contents.lootbox;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.stream.IntStream;

public class ChristmasFurnitureLootbox extends CustomLootbox {
        public ChristmasFurnitureLootbox() {
            super(
                    OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_LOOTBOX,
                    "omc_christmas:christmas_furniture",
                    TranslationManager.translation("itemsadder.omc_christmas.christmas_lootbox.name"),
                    OMCRegistry.CUSTOM_LOOT_TABLES.CHRISTMAS_FURNITURE,
                    new LootboxOptions(
                            InventorySize.NORMAL,
                            60,
                            IntStream.range(10, 17).boxed().toList(),
                            13
                    )
            );
        }
    }
