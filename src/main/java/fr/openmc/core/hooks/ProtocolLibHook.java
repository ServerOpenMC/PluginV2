package fr.openmc.core.hooks;

import fr.openmc.core.bootstrap.hooks.Hooks;

import java.util.Collections;
import java.util.Set;

public class ProtocolLibHook extends Hooks {
    public static boolean isEnable() {
        return Hooks.isEnabled(ProtocolLibHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Collections.singleton("ProtocolLib");
    }
}
