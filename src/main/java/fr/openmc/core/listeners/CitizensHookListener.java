package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.api.CitizensApi;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensHookListener implements Listener {
    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
                Bukkit.getLogger().info("[OpenMC] Citizens est chargé, l’API est prête.");
                System.out.println(OMCPlugin.getInstance().getServer().getServicesManager().load(LuckPerms.class));
                CitizensApi.setHasCitizens(true, OMCPlugin.getInstance().getServer().getServicesManager().load(CitizensAPI.class));
    }
    private static NPCRegistry registry;

    public static NPCRegistry getRegistry() {
        if (registry == null) {
            Bukkit.getLogger().warning("[NPCManager] Tentative d'accès au registry avant readiness.");
        }
        return registry;
    }
}
