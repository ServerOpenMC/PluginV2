package fr.openmc.api.hooks;

import org.bukkit.Bukkit;

public class PapiHook {
    private static boolean hasPAPI;

    public PapiHook() {
        hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * Retourne si l'instance a PlaceholderAPI
     */
    public static boolean hasPAPI() {
        return hasPAPI;
    }


}
