package fr.openmc.api.hooks;

import lombok.Getter;
import org.bukkit.Bukkit;

public class FancyNpcsHook {
    @Getter
    private static boolean hasFancyNpc = false;

    public FancyNpcsHook() {
        hasFancyNpc = Bukkit.getPluginManager().isPluginEnabled("FancyNpcs");
    }
}