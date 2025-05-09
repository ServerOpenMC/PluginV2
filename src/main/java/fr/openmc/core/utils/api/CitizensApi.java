package fr.openmc.core.utils.api;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CitizensApi {
    private static boolean hasCitizens = false;
    private static CitizensAPI api = null;

    public CitizensApi() {
        Plugin citizensPlugin = Bukkit.getPluginManager().getPlugin("Citizens");

        if (citizensPlugin != null && citizensPlugin.isEnabled()) {
            hasCitizens = true;
        } else {
            hasCitizens = false;
        }
    }

    public static boolean hasCitizens() {
        return hasCitizens;
    }

    public static void setHasCitizens(boolean value, CitizensAPI api1) {
        api = api1;
        hasCitizens = value;
    }
}