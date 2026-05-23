package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.Registry;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> {

    @Override
    public void postInit() {
        // ** REGISTRER LOOT TABLES **

    }

    public void register(CustomLootTable table) {
        register(table.getName(), table);
    }

    public void register(CustomLootTable... tables) {
        for (CustomLootTable table : tables) {
            register(table);
        }
    }
}