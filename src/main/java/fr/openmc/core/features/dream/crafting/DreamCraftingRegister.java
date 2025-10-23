package fr.openmc.core.features.dream.crafting;

import fr.openmc.core.OMCPlugin;

public class DreamCraftingRegister {
    public static void init() {
        OMCPlugin.registerEvents(
                new DreamCraftingListener()
        );
    }
}
