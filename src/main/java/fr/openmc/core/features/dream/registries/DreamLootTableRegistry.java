package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.CloudVaultLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;
import fr.openmc.core.registry.loottable.CustomLootTable;

import java.util.HashSet;
import java.util.Set;

public class DreamLootTableRegistry {
    public static final CustomLootTable CLOUD_FISHING = create(new CloudFishingLootTable());
    public static final CustomLootTable CLOUD_VAULT = create(new CloudVaultLootTable());
    public static final CustomLootTable METAL_DETECTOR = create(new MetalDetectorLootTable());

    public static Set<CustomLootTable> LOOT_TABLE_REGISTRY;

    private static CustomLootTable create(CustomLootTable item) {
        if (LOOT_TABLE_REGISTRY == null)
            LOOT_TABLE_REGISTRY = new HashSet<>();

        LOOT_TABLE_REGISTRY.add(item);
        return item;
    }

    public static void init() {
        OMCRegistry.CUSTOM_LOOT_TABLES.register(LOOT_TABLE_REGISTRY);
    }
}
