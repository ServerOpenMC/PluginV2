package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.api.CitizensApi;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensHookListener implements Listener {
    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        Bukkit.getLogger().info("[OpenMC] Citizens est chargé, l’API est prête.");
        System.out.println(OMCPlugin.getInstance().getServer().getServicesManager().load(CitizensAPI.class));
        CitizensApi.setHasCitizens(true);
    }
}
