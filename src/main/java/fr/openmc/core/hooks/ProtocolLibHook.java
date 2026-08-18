package fr.openmc.core.hooks;

import fr.openmc.core.registry.hooks.Hooks;

public class ProtocolLibHook extends Hooks {
    public boolean isEnable() {
        return Hooks.isEnabled(ProtocolLibHook.class);
    }

    @Override
    protected String getPluginName() {
        return "ProtocolLib";
    }
}
