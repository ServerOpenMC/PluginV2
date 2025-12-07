package fr.openmc.core.features.dream.registries;

import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.features.dream.registries.loottable.CloudFishingLootTable;
import fr.openmc.core.features.dream.registries.loottable.MetalDetectorLootTable;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;

public class DreamLootTableRegistry {
    private final static Set<DreamLootTable> DREAM_LOOT_TABLE_REGISTRY = Set.of(
            new CloudFishingLootTable(),
            new MetalDetectorLootTable()
    );
    static final HashMap<Key, DreamLootTable> dreamLootTables = new HashMap<>();

    public static void init() {
        for (DreamLootTable dreamLootTable : DREAM_LOOT_TABLE_REGISTRY) {
            dreamLootTables.put(dreamLootTable.getKey(), dreamLootTable);
        }
    }

    public static void register(Key key, DreamLootTable dreamLootTable) {
        dreamLootTables.put(key, dreamLootTable);
    }

    @Nullable
    public static DreamLootTable getByKey(Key key) {
        return dreamLootTables.get(key);
    }
}
