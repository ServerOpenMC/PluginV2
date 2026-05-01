package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

public class PapiHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(PapiHook.class);
    }

    @Override
    protected String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    protected void init() {
        // not used
    }

}
