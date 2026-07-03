package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing.BasicFishLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing.MiraculousFishLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.fishing.SeaCreatureLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox.EpicFishingTreasureLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox.FishingFurnitureLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox.LegendaryFishingTreasureLootTable;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.loottable.lootbox.RareFishingTreasureLootTable;
import fr.openmc.core.registry.loottable.contents.MachineBallLootTable;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> implements KeyedRegistry<String, CustomLootTable> {

    // ** REGISTER LOOT TABLE **
    public final CustomLootTable MACHINE_BALL = register(new MachineBallLootTable());

    public final CustomLootTable MIRACULOUS_FISHING = register(new MiraculousFishLootTable());
    public final CustomLootTable BASIC_FISHING = register(new BasicFishLootTable());
    public final CustomLootTable SEA_CREATURE = register(new SeaCreatureLootTable());

    public final CustomLootTable FISHING_FURNITURE = register(new FishingFurnitureLootTable());
    public final CustomLootTable RARE_FISHING_TREASURE = register(new RareFishingTreasureLootTable());
    public final CustomLootTable EPIC_FISHING_TREASURE = register(new EpicFishingTreasureLootTable());
    public final CustomLootTable LEGENDARY_FISHING_TREASURE = register(new LegendaryFishingTreasureLootTable());

    @Override
    public String key(CustomLootTable registryObject) {
        return registryObject.getNamespace();
    }
}