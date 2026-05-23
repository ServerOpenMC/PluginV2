package fr.openmc.core.features.dream.registries;

import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.CloudVaultLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;

public class DreamLootTableRegistry {
    public static void init() {
        CustomLootTableRegistry.register(
                new CloudFishingLootTable(),
                new MetalDetectorLootTable(),
                new CloudVaultLootTable()
        );
    }
}
