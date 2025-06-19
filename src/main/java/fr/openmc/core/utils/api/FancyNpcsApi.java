package fr.openmc.core.utils.api;

import org.bukkit.Bukkit;

public class FancyNpcsApi {
    private static boolean hasFancyNpc = false;

    public FancyNpcsApi() {
        hasFancyNpc = Bukkit.getPluginManager().isPluginEnabled("FancyNpcs");
    }

    /**
     * Retourne si l'instance a FancyNpc
     */
    public static boolean hasFancyNpc() {
        return hasFancyNpc;
    }

    /**
     * Set if the instance has FancyNpc
     *
     * @param hasFancyNpc true if the instance has FancyNpc, false otherwise
     */
    public static void setHasFancyNpc(boolean hasFancyNpc) {
        FancyNpcsApi.hasFancyNpc = hasFancyNpc;
    }
}