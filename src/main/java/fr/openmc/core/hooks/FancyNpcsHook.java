package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

public class FancyNpcsHook extends Hooks {
    public static long FANCY_INIT_DELAY = 20L * 30; // 30 seconds

    public static boolean isEnable() {
        return Hooks.isEnabled(FancyNpcsHook.class);
    }

    @Override
    protected String getPluginName() {
        return "FancyNpcs";
    }

    @Override
    protected void init() {
        // not used
    }
}