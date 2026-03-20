package fr.openmc.api.hooks;

import lombok.Getter;
import org.bukkit.Bukkit;

public class FancyNpcsHook {
    @Getter
    private static boolean hasFancyNpc = false;

    public static long FANCY_INIT_DELAY = 20L * 30; // 30 seconds

    public static void init() {
        hasFancyNpc = Bukkit.getPluginManager().isPluginEnabled("FancyNpcs");
    }

}