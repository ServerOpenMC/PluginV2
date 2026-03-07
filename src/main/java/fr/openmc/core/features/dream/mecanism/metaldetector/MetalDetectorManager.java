package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MetalDetectorManager {
    public static final Map<UUID, MetalDetectorTask> hiddenChests = new HashMap<>();

    public static final CustomLootTable METAL_DETECTOR_LOOT_TABLE = CustomLootTableRegistry.getByName("omc_dream:metal_detector");

    public static void init() {
        OMCPlugin.registerEvents(
                new MetalDetectorListener()
        );
    }
}
