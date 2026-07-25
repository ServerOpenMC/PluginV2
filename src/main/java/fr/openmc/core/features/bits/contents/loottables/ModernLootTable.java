package fr.openmc.core.features.bits.contents.loottables;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModernLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.MODERN_BOX.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_bits:modern_box";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_BED,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_BOARD,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_CABINET,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_CARPET,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_CHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_COMPUTERCHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_COMPUTERDESK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_LAMP,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_MACBOOK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_PICTURE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_PLANTPOT,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_SHELF,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_SOFA_01,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_SOFA_02,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_TABLE_01,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_TABLE_02,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_TABLE_03,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_TV,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_WARDROBE_01,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MODERN_FURNITURE_PACK_VOL2_WARDROBE_02,
                        0.2,
                        1
                )
        ));
    }
}
