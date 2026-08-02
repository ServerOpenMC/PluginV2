package fr.openmc.core.features.bits.contents.lootboxes;

import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import fr.openmc.core.registry.lootboxes.LootboxOptions;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.stream.IntStream;

public class OfficeLootbox extends CustomLootbox {
    public OfficeLootbox() {
        super(
                OMCRegistry.CUSTOM_ITEMS.OFFICE_BOX,
                "omc_bits:office_box",
                TranslationManager.translation("feature.bits.lootbox.office.name"),
                OMCRegistry.CUSTOM_LOOT_TABLES.OFFICE_BOX,
                new LootboxOptions(
                        InventorySize.NORMAL,
                        60,
                        IntStream.range(10, 17).boxed().toList(),
                        13
                )
        );
    }
}
