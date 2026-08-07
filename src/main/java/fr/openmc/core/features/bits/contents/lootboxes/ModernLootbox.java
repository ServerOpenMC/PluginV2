package fr.openmc.core.features.bits.contents.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.stream.IntStream;

public class ModernLootbox extends CustomLootbox {
    public ModernLootbox() {
        super(
                OMCRegistry.CUSTOM_ITEMS.MODERN_BOX,
                "omc_bits:modern_box",
                TranslationManager.translation("feature.bits.lootbox.modern.name"),
                OMCRegistry.CUSTOM_LOOT_TABLES.MODERN_BOX,
                new LootboxOptions(
                        InventorySize.NORMAL,
                        60,
                        IntStream.range(10, 17).boxed().toList(),
                        13
                )
        );
    }
}

