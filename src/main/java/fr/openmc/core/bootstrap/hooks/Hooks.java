package fr.openmc.core.bootstrap.hooks;

import fr.openmc.core.OMCPlugin;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Hooks {
    private static final Map<Class<? extends Hooks>, Boolean> ENABLED = new ConcurrentHashMap<>();

    public void startInit() {
        String pluginName = getPluginName();

        PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
        boolean enabled = pluginManager.getPlugin(pluginName) != null
                && pluginManager.isPluginEnabled(pluginName);
        ENABLED.put(getClass(), enabled);
        if (enabled) {
            init();
            OMCPlugin.getInstance().logSuccessMessage("Hook " + pluginName + " activé.");
            return;
        }
        OMCPlugin.getInstance().logErrorMessage("Hook " + pluginName + " non activé.");
    }

    public static boolean isEnabled(Class<? extends Hooks> hookClass) {
        return ENABLED.getOrDefault(hookClass, false);
    }

    protected abstract String getPluginName();
    protected abstract void init();
}
