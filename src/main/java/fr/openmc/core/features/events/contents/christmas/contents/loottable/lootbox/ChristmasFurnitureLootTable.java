package fr.openmc.core.features.events.contents.christmas.contents.loottable.lootbox;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChristmasFurnitureLootTable extends CustomLootTable {
    @Override
    public @NonNull Component getName() {
        return OMCRegistry.CUSTOM_LOOTBOXES.CHRISTMAS_FURNITURE.getName();
    }

    @Override
    public String getNamespace() {
        return "omc_christmas:christmas_furniture";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return new LinkedHashSet<>(List.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_RED_RIBBON,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_YELLOW_RIBBON,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_WHITE_RIBBON,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_RED_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_RED_WHITE_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_YELLOW_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_GREEN_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_GREEN_RED_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_BLUE_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_WHITE_GIFT_BOX_BLOCK,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_RED_TWIST_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_RED_SWIRL_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_YELLOW_TWIST_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_YELLOW_SWIRL_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_GREEN_TWIST_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_GREEN_SWIRL_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_BLUE_TWIST_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_BLUE_SWIRL_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_PURPLE_TWIST_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_PURPLE_SWIRL_CANDY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_CANDLES,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_CANDY_CANE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_CANDY_CANE2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_CHRISTMAS_TREE,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_CHRISTMAS_WREATH,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_DEER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_ELVES,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_HOLLY,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_JINGLE_BELLS,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_LONG_GIFT_BOX,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_NUTCRACKER,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_OPEN_GIFT_BOX,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_PENGUIN,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SANTA_CLAUS,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SANTA_CLAUS_BAG,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SANTA_DECORATION,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SANTA_DECORATION2,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SANTA_SLEIGH,
                        0.2,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.CHRISTMAS_PACK_SNOWMAN,
                        0.2,
                        1
                )
        ));
    }
}