package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();
        
        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI(this);
        new PapiAPI();

        /* MANAGERS */
        dbManager = new DatabaseManager();
        ContestManager contestManager = new ContestManager(this);
        ContestPlayerManager contestPlayerManager = new ContestPlayerManager();
        new CommandsManager();
        CustomItemRegistry.init();
        new SpawnManager(this);
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        contestPlayerManager.setContestManager(contestManager); // else ContestPlayerManager crash because ContestManager is null
        contestManager.setContestPlayerManager(contestPlayerManager);
        new MotdUtils(this);

        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        ContestManager.getInstance().saveContestData();
        ContestManager.getInstance().saveContestPlayerData();
        if (dbManager != null) {
            try {
                dbManager.close();
            } catch (SQLException e) {
                getLogger().severe("Impossible de fermer la connexion à la base de données");
            }
        }

        getLogger().info("Plugin désactivé");
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }
}
