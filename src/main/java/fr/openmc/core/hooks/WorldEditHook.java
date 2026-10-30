package fr.openmc.core.hooks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.hooks.Hooks;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class WorldEditHook extends Hooks {

    private static final String WE = "WorldEdit";
    private static final String FAWE = "FastAsyncWorldEdit";

    public static boolean isEnable() {
        return Hooks.isEnabled(WorldEditHook.class);
    }

    @Override
    protected Set<String> getPluginsName() {
        return Set.of(WE, FAWE);
    }

    @Override
    public boolean arePluginEnabled() {
        return isLoaded(WE) || isLoaded(FAWE);
    }

    private static boolean isLoaded(String name) {
        PluginManager pm = OMCPlugin.getInstance().getServer().getPluginManager();
        return pm.getPlugin(name) != null && pm.isPluginEnabled(name);
    }
}