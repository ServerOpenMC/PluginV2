package fr.openmc.core.registry.lootboxes;

import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.bits.contents.lootboxes.KitchenLootbox;
import fr.openmc.core.features.bits.contents.lootboxes.MedievalLootbox;
import fr.openmc.core.features.bits.contents.lootboxes.ModernLootbox;
import fr.openmc.core.features.bits.contents.lootboxes.OfficeLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.EpicFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.FishingFurnitureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.LegendaryFishingTreasureLootbox;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.lootboxes.RareFishingTreasureLootbox;
import fr.openmc.core.registry.lootboxes.contents.MachineBallLootbox;
import fr.openmc.core.registry.lootboxes.listener.DesactivateFireworkDamageListener;

import java.util.Set;

public class CustomLootboxRegistry extends Registry<String, CustomLootbox>
        implements KeyedRegistry<String, CustomLootbox>, HasListeners {

    // ** REGISTER LOOTBOX **
    public final CustomLootbox MACHINE_BALL = register(new MachineBallLootbox());

    public final CustomLootbox FISHING_FURNITURE = register(new FishingFurnitureLootbox());
    public final CustomLootbox RARE_FISHING_TREASURE = register(new RareFishingTreasureLootbox());
    public final CustomLootbox EPIC_FISHING_TREASURE = register(new EpicFishingTreasureLootbox());
    public final CustomLootbox LEGENDARY_FISHING_TREASURE = register(new LegendaryFishingTreasureLootbox());


    public final CustomLootbox MODERN_BOX = register(new ModernLootbox());
    public final CustomLootbox OFFICE_BOX = register(new OfficeLootbox());
    public final CustomLootbox MEDIEVAL_BOX = register(new MedievalLootbox());
    public final CustomLootbox KITCHEN_BOX = register(new KitchenLootbox());


    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(DesactivateFireworkDamageListener::new);
    }

    @Override
    public String key(CustomLootbox registryObject) {
        return registryObject.getNamespace();
    }
}
