package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

public class ProtocolLibHook extends Hooks {
    @Override
    protected String getPluginName() {
        return "ProtocolLib";
    }

    @Override
    protected void init() {
        // not used
    }
}
