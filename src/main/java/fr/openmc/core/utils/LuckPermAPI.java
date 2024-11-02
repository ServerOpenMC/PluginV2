package fr.openmc.core.utils;

import fr.openmc.core.OMCPlugin;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getLogger;

public class LuckPermAPI {
    public LuckPermAPI(OMCPlugin plugin) {
        // INIT LUCKPERMS

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            plugin.lpApi = provider.getProvider();
        } else {
            getLogger().severe("LuckPerms is missing!");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
    }

    // TODO: Mettre les méthodes addPermissions, ou autres si besoin ici
}
