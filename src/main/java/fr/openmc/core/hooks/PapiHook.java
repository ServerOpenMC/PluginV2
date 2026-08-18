package fr.openmc.core.hooks;

import fr.openmc.core.registry.hooks.Hooks;

public class PapiHook extends Hooks {
    public boolean isEnable() {
        return Hooks.isEnabled(PapiHook.class);
    }

    @Override
    protected String getPluginName() {
        return "PlaceholderAPI";
    }
}
