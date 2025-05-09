package fr.openmc.core.utils.api;

import org.bukkit.Bukkit;

public class PapiApi {
    private static boolean hasPAPI;

    public PapiApi() {
        hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * Retourne si l'instance a PlaceholderAPI
     */
    public static boolean hasPAPI() {
        return hasPAPI;
    }


}
