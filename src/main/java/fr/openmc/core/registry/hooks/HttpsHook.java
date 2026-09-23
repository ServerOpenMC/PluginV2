package fr.openmc.core.registry.hooks;

import java.util.Collections;
import java.util.Set;

public abstract class HttpsHook extends Hooks {
    abstract public String getName();

    public boolean isEnable() {
        return Hooks.isEnabled(HttpsHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton(getName());
    }
}
