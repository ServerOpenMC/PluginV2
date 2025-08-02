package fr.openmc.core.utils.api;

import org.bukkit.Bukkit;

public class ItemsAdderApi {
    private static boolean hasItemAdder;

    public ItemsAdderApi() {
        hasItemAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    /**
     * Retourne si l'instance a ItemAdder
     */
    public static boolean hasItemAdder() {
        return hasItemAdder;
    }

}
