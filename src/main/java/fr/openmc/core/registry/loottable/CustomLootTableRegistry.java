package fr.openmc.core.registry.loottable;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.loottable.MiraculousFishLootTable;
import fr.openmc.core.registry.loottable.contents.MachineBallLootTable;

public class CustomLootTableRegistry extends Registry<String, CustomLootTable> implements KeyedRegistry<String, CustomLootTable> {

    // ** REGISTER LOOT TABLE **
    public final CustomLootTable MACHINE_BALL = register(new MachineBallLootTable());

    public final CustomLootTable MIRACULOUS_FISHING = register(new MiraculousFishLootTable());

    @Override
    public String key(CustomLootTable registryObject) {
        return registryObject.getNamespace();
    }
}