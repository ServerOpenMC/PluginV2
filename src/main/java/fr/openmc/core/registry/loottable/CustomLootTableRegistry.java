package fr.openmc.core.registry.loottable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomLootTableRegistry {
    static final HashMap<String, CustomLootTable> lootTables = new HashMap<>();

    public static void init() {
        // ** REGISTRER LOOT TABLES **


    }

    public static void register(CustomLootTable table) {
        lootTables.put(table.getName(), table);
    }

    public static void register(CustomLootTable... tables) {
        for (CustomLootTable table : tables) {
            register(table);
        }
    }

    @Nullable
    public static CustomLootTable getByName(String name) {
        return lootTables.get(name);
    }

    public static HashSet<String> getNames() {
        return new HashSet<>(lootTables.keySet());
    }
}