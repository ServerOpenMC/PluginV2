package fr.openmc.core.hooks;

import fr.openmc.core.lifecycle.hooks.Hooks;

public class ProtocolLibHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(ProtocolLibHook.class);
    }

    @Override
    protected String getPluginName() {
        return "ProtocolLib";
    }
}
