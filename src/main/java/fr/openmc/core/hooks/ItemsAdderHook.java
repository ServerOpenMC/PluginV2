package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

public class ItemsAdderHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(ItemsAdderHook.class);
    }

    @Override
    protected String getPluginName() {
        return "ItemsAdder";
    }

    @Override
    protected void init() {
        // not used
    }

}
