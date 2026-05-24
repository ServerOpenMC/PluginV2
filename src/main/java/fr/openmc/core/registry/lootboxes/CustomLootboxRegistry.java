package fr.openmc.core.registry.lootboxes;

import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.registry.lootboxes.contents.MachineBallLootbox;

public class CustomLootboxRegistry extends Registry<String, CustomLootbox> {

    @Override
    public void postInit() {
        // ** REGISTRER LOOTBOXES **
        register(
                new MachineBallLootbox()
        );
    }

    public void register(CustomLootbox... boxes) {
        for (CustomLootbox box : boxes) {
            register(box.getNamespace(), box);
        }
    }
}
