package fr.openmc.core.utils;

import org.bukkit.Bukkit;

public class PapiAPI {
    private static boolean hasPAPI;

    public PapiAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            hasPAPI = false;
            return;
        } else {
            hasPAPI = true;
        }
    }

    public static boolean hasPAPI() {
        return hasPAPI;
    }


}
