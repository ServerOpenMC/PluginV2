package fr.openmc.core.bootstrap.hooks;

public abstract class HttpsHook extends Hooks {
    abstract public String getName();

    public static boolean isEnable() {
        return Hooks.isEnabled(HttpsHook.class);
    }

    @Override
    protected String getPluginName() {
        return getName();
    }
}
