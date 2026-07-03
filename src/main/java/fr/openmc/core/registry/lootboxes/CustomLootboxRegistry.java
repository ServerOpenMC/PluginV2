package fr.openmc.core.registry.lootboxes;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.EpicFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.FishingFurnitureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.LegendaryFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.RareFishingTreasureLootbox;
import fr.openmc.core.registry.lootboxes.contents.MachineBallLootbox;

public class CustomLootboxRegistry extends Registry<String, CustomLootbox> implements KeyedRegistry<String, CustomLootbox> {

    // ** REGISTER LOOTBOX **
    public final CustomLootbox MACHINE_BALL = register(new MachineBallLootbox());

    public final CustomLootbox FISHING_FURNITURE = register(new FishingFurnitureLootbox());
    public final CustomLootbox RARE_FISHING_TREASURE = register(new RareFishingTreasureLootbox());
    public final CustomLootbox EPIC_FISHING_TREASURE = register(new EpicFishingTreasureLootbox());
    public final CustomLootbox LEGENDARY_FISHING_TREASURE = register(new LegendaryFishingTreasureLootbox());

    @Override
    public String key(CustomLootbox registryObject) {
        return registryObject.getNamespace();
    }
}
