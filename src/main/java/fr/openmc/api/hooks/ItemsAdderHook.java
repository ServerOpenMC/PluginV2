package fr.openmc.api.hooks;

import lombok.Getter;
import org.bukkit.Bukkit;

public class ItemsAdderHook {
    @Getter
    private static boolean hasItemAdder;

    public static void init() {
        hasItemAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }
}
