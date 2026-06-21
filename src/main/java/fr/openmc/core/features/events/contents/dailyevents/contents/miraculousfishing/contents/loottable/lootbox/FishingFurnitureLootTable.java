package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.ItemLoot;

import java.util.Set;

public class FishingFurnitureLootTable extends CustomLootTable {
    @Override
    public String getNamespace() {
        return "omc_daily_events:fishing_furniture";
    }

    @Override
    public Set<CustomLoot> getLoots() {
        return Set.of(
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_BLUE_FISH,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_CYAN_FISH,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_ORANGE_FISH,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_RED_FISH,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_BOAT,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_CHAIR,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_FISH_BOX,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_FISH_RACK,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_FISHING_POLE,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_FISHINGPOLE_RACK,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_FLOATIE,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_HANGING_FISH,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_LANDING_NET,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_LARGE_FISHNET,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_LOBSTER_TRAP,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_STAND,
                        0.2,
                        1,
                        1
                ),
                new ItemLoot(
                        OMCRegistry.CUSTOM_ITEMS.FISHERMAN_TABLE,
                        0.2,
                        1,
                        1
                )
        );
    }
}