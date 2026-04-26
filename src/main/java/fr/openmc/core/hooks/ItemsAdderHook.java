package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;
import lombok.Getter;

public class ItemsAdderHook extends Hooks {
    @Getter
    private static boolean enable;

    @Override
    protected String getPluginName() {
        return "ItemsAdder";
    }

    @Override
    protected void init() {
        // not used
    }

}
