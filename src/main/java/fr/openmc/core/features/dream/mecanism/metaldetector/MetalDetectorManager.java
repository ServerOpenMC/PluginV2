package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.features.dream.registries.DreamLootTableRegistry;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MetalDetectorManager {
    public static final Map<UUID, MetalDetectorTask> hiddenChests = new HashMap<>();

    public static final DreamLootTable METAL_DETECTOR_LOOT_TABLE = DreamLootTableRegistry.getByKey(Key.key("dream:metal_detector"));

    public static void init() {
        OMCPlugin.registerEvents(
                new MetalDetectorListener()
        );
    }
}
