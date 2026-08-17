package fr.openmc.core.hooks;

import fr.openmc.core.lifecycle.hooks.Hooks;

public class PapiHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(PapiHook.class);
    }

    @Override
    protected String getPluginName() {
        return "PlaceholderAPI";
    }
}
