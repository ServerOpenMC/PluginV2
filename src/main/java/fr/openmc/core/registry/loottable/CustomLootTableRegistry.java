package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.loottable.contents.MachineBallLootTable;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> implements KeyedRegistry<String, CustomLootTable> {

    @Override
    public void postInit() {
        // ** REGISTRER LOOT TABLES **
        register(
                new MachineBallLootTable()
        );
    }

    @Override
    public String key(CustomLootTable registryObject) {
        return registryObject.getNamespace();
    }
}