package fr.openmc.core.features.bits.contents.loottables;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MedievalLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.MEDIEVAL_BOX.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_bits:medieval_box";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BAR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BAR_CHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BAR_CORNER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BAR_SIGN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BARREL_STACK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BEER_TANK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_BEER_TANK_WALL,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_CANDLE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_CARPET,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_CHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_DART_BOARD,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_DRINKING_HORN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_GLASS_HANGING_BAR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_1,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_3,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_SHELF_4,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_TABLE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_WALL_LAMP,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.MEDIEVAL_TAVERN_FURNITURE_V1_WANTD_PAPER,
                        0.2,
                        1
                )
        ));
    }
}
