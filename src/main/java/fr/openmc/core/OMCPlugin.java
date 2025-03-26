package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.ScoreboardManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.listeners.CubeListener;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.WorldGuardApi;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.translation.TranslationManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    @Getter static TranslationManager translationManager;
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI();
        new PapiAPI();
        new WorldGuardApi();

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        CustomItemRegistry.init();
        ContestManager contestManager = new ContestManager(this);
        ContestPlayerManager contestPlayerManager = new ContestPlayerManager();
        new SpawnManager(this);
        new MascotsManager(this); // laisser avant CityManager
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new ScoreboardManager();
        new HomesManager();
        new HomeUpgradeManager(HomesManager.getInstance());
        new TPAManager();
        new FriendManager();

        contestPlayerManager.setContestManager(contestManager); // else ContestPlayerManager crash because ContestManager is null
        contestManager.setContestPlayerManager(contestPlayerManager);
        new MotdUtils(this);
        translationManager = new TranslationManager(this, new File(this.getDataFolder(), "translations"), "fr");
        translationManager.loadAllLanguages();
        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        HomesManager.getInstance().saveHomesData();
        ContestManager.getInstance().saveContestData();
        ContestManager.getInstance().saveContestPlayerData();

        MascotsManager.saveMascots(MascotsManager.mascots);
        MascotsManager.saveFreeClaims(MascotsManager.freeClaim);

        CubeListener.clearCube(CubeListener.currentLocation);
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
