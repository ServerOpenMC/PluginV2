package fr.openmc.core.features.bits.contents.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.stream.IntStream;

public class KitchenLootbox extends CustomLootbox {
    public KitchenLootbox() {
        super(
                OMCRegistry.CUSTOM_ITEMS.KITCHEN_BOX,
                "omc_bits:kitchen_box",
                TranslationManager.translation("feature.bits.lootbox.kitchen.name"),
                OMCRegistry.CUSTOM_LOOT_TABLES.KITCHEN_BOX,
                new LootboxOptions(
                        InventorySize.NORMAL,
                        60,
                        IntStream.range(10, 17).boxed().toList(),
                        13
                )
        );
    }
}
