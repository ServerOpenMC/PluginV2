package fr.openmc.api.hooks;

import lombok.Getter;
import org.bukkit.Bukkit;

public class PapiHook {
    @Getter
    private static boolean hasPAPI;

    public PapiHook() {
        hasPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
