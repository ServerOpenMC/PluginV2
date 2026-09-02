package fr.openmc.core.bootstrap.hooks;

import java.util.Collections;
import java.util.Set;

public abstract class HttpsHook extends Hooks {
    abstract public String getName();

    public static boolean isEnable() {
        return Hooks.isEnabled(HttpsHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton(getName());
    }
}
