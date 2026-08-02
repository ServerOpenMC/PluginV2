package fr.openmc.core.features.bits.contents.loottables;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OfficeLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.OFFICE_BOX.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_bits:office_box";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_BOARD_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_BOARD_2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_CHAIRCEO,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_CHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_COMPUTER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_CUPBOARD_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_CUPBOARD_2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_CUPBOARD_3,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_DRAWER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_FILE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_LAMP,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_POTTEDPLANTS,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_PRINTER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_PROJECTOR_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_PROJECTOR_2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_RUBBISHBIN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_SOFA_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_SOFA_2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_TABLECEO,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_TABLE_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.OFFICE_FURNITURE_V2_TABLE_2,
                        0.2,
                        1
                )
        ));
    }
}
