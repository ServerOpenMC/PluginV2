package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.loottable.contents.MachineBallLootTable;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> {

    @Override
    public void postInit() {
        // ** REGISTRER LOOT TABLES **
        register(
                new MachineBallLootTable()
        );
    }

    public void register(CustomLootTable... tables) {
        for (CustomLootTable table : tables) {
            register(table.getNamespace(), table);
        }
    }
}