package fr.openmc.core.registry.lootboxes;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.lootboxes.contents.MachineBallLootbox;

public class CustomLootboxRegistry extends Registry<String, CustomLootbox> implements KeyedRegistry<String, CustomLootbox> {

    // ** REGISTER LOOTBOX **
    public final CustomLootbox MACHINE_BALL = register(new MachineBallLootbox());

    @Override
    public String key(CustomLootbox registryObject) {
        return registryObject.getNamespace();
    }
}
