package fr.openmc.core.features.bits.contents.loottables;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class KitchenLootTable extends CustomLootTable {
    @Override
    public Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.KITCHEN_BOX.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_bits:kitchen_box";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_BARSTOOL,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_BIN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_CHAIR,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_COOKERHOOD,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_DISH,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_DISH_PILE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_ELEMENT,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_FRIDGE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_KNIFES,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_MICROWAVE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_PAN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_POT,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_SINK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_STOVE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_WALL_ELEMENT,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_WINE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.KITCHEN_WINERACK,
                        0.2,
                        1
                )
        ));
    }
}
