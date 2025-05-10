package fr.openmc.core.utils.api;

import org.bukkit.Bukkit;

public class ItemAdderApi {
    private static boolean hasItemAdder;

    public ItemAdderApi() {
        hasItemAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");;
    }

    /**
     * Retourne si l'instance a ItemAdder
     */
    public static boolean hasItemAdder() {
        return hasItemAdder;
    }

}
