package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

import java.util.Collections;
import java.util.Set;

public class PapiHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(PapiHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton("PlaceholderAPI");
    }
}
