package fr.openmc.core.bootstrap.hooks;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

public abstract class Hooks {
    private String pluginName;
    @Getter
    private static boolean enable = false;

    public void startInit() {
        this.pluginName = getPluginName();

        PluginManager pluginManager = OMCPlugin.getInstance().getServer().getPluginManager();
        if (pluginManager.getPlugin(pluginName) != null) {
            enable = true;
            init();
            OMCPlugin.getInstance().logSuccessMessage("Hook " + pluginName + " activé.");
        } else {
            OMCPlugin.getInstance().logErrorMessage("Hook " + pluginName + " non activé.");
        }
    }

    protected abstract String getPluginName();
    protected abstract void init();

    public boolean isEnabled() {
        return OMCPlugin.getInstance().getServer().getPluginManager().isPluginEnabled(pluginName);
    }
}
