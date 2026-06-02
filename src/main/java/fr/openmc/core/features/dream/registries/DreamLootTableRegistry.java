package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.CloudVaultLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;

public class DreamLootTableRegistry {
    public static void init() {
        OMCRegistry.CUSTOM_LOOT_TABLES.register(
                new CloudFishingLootTable(),
                new MetalDetectorLootTable(),
                new CloudVaultLootTable()
        );
    }
}
