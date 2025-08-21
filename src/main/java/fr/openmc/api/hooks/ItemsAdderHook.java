package fr.openmc.api.hooks;

import org.bukkit.Bukkit;

public class ItemsAdderHook {
    private static boolean hasItemAdder;

    public ItemsAdderHook() {
        hasItemAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    /**
     * Retourne si l'instance a ItemAdder
     */
    public static boolean hasItemAdder() {
        return hasItemAdder;
    }

}
