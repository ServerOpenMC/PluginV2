package fr.openmc.core.utils.api;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;

public class CitizensApi {
    @Getter
    private static CitizensAPI api;
    private static boolean hasCitizens;

    public CitizensApi() {
        if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
            hasCitizens = false;
            return;
        } else {
            hasCitizens = true;
        }

        api = OMCPlugin.getInstance().getServer().getServicesManager().load(CitizensAPI.class);
    }

    /**
     * Retourne si l'instance a Citizens
     */
    public static boolean hasCitizens() {
        return hasCitizens;
    }
}
