package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> implements KeyedRegistry<String, CustomLootTable> {

    @Override
    public void postInit() {
        // ** REGISTRER LOOT TABLES **

    }

    @Override
    public String key(CustomLootTable registryObject) {
        return registryObject.getName();
    }
}