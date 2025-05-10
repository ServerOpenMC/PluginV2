package fr.openmc.core.utils.api;

import org.bukkit.Bukkit;

public class ItemAdderApi {
    private static boolean hasItemAdder;

    public ItemAdderApi() {
        if (Bukkit.getPluginManager().getPlugin("ItemAdder") == null) {
            hasItemAdder = false;
            return;
        } else {
            hasItemAdder = true;
        }
    }

    /**
     * Retourne si l'instance a ItemAdder
     */
    public static boolean hasItemAdder() {
        return hasItemAdder;
    }

}
