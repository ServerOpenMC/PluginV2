package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.CloudVaultLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;

public class DreamLootTableRegistry extends SubRegistry<String, CustomLootTable> {
    public final CustomLootTable CLOUD_FISHING = register(new CloudFishingLootTable());
    public final CustomLootTable CLOUD_VAULT = register(new CloudVaultLootTable());
    public final CustomLootTable METAL_DETECTOR = register(new MetalDetectorLootTable());


    @Override
    public KeyedRegistry<String, ? super CustomLootTable> getParentRegistry() {
        return OMCRegistry.CUSTOM_LOOT_TABLES;
    }
}
