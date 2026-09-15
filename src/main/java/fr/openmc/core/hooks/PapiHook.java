package fr.openmc.core.hooks;

import fr.openmc.core.registry.hooks.Hooks;

import java.util.Collections;
import java.util.Set;

public class PapiHook extends Hooks {
    public boolean isEnable() {
        return Hooks.isEnabled(PapiHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton("PlaceholderAPI");
    }
}
