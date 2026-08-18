package fr.openmc.core.registry.hooks;

public abstract class HttpsHook extends Hooks {
    abstract public String getName();

    public boolean isEnable() {
        return Hooks.isEnabled(HttpsHook.class);
    }

    @Override
    protected String getPluginName() {
        return getName();
    }
}
