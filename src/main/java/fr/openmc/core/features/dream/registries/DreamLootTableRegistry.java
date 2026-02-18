package fr.openmc.core.features.dream.registries;

import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;

public class DreamLootTableRegistry {
    public static void init() {
        CustomLootTableRegistry.register(
                new CloudFishingLootTable(),
                new MetalDetectorLootTable()
        );
    }
}
