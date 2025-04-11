package fr.openmc.core.utils;

import org.bukkit.Bukkit;

public class PapiAPI {
    private static boolean hasPAPI;

    public PapiAPI() {
        hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * Retourne si l'instance a PlaceholderAPI
     */
    public static boolean hasPAPI() {
        return hasPAPI;
    }


}
