package fr.openmc.core;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.core.commands.admin.freeze.FreezeManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.scoreboards.ScoreboardManager;
import fr.openmc.core.features.scoreboards.TabList;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.listeners.CubeListener;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.api.*;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.translation.TranslationManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class OMCPlugin extends JavaPlugin {
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
        // TODO: faire des messages a envoyer dans la console disant, la version du plugin, version de minecraft, si chaque api sont bien connecté ou manquant, et les versions des plugins lié a OpenMC ?
        new LuckPermsApi();
        new PapiApi();
        new WorldGuardApi();
        new ItemAdderApi();
        new FancyNpcApi();

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        CustomItemRegistry.init();
        new SpawnManager(this);
        new UpdateManager();
        new MascotsManager(this); // laisser avant CityManager
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new MayorManager();
        new BankManager();
        new ScoreboardManager();
        new HomesManager();
        new TPAManager();
        new FreezeManager();
        new QuestsManager();
        new TabList();
        if (!OMCPlugin.isUnitTestVersion())
            new LeaderboardManager();
        new AdminShopManager(this);

        if (!OMCPlugin.isUnitTestVersion()){
            new CompanyManager();// laisser apres Economy Manager
        }
        new MotdUtils(this);
        translationManager = new TranslationManager(this, new File(this.getDataFolder(), "translations"), "fr");
        translationManager.loadAllLanguages();

        /* LOAD */
        DynamicCooldownManager.loadCooldowns();

        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        // SAUVEGARDE

        // - Maires
        MayorManager.saveMayorConstant();
        MayorManager.savePlayersVote();
        MayorManager.saveMayorCandidates();
        MayorManager.saveCityMayors();
        MayorManager.saveCityLaws();

        // - Home
        CompanyManager.saveAllCompanies();
        CompanyManager.saveAllShop();

        HomesManager.saveHomesData();

        // - Contest
        ContestManager.saveContestData();
        ContestManager.saveContestPlayerData();
        QuestsManager.getInstance().saveQuests();

        // - Mascottes
        MascotsManager.saveMascots(MascotsManager.mascots);
        CityManager.saveFreeClaims(CityManager.freeClaim);

        // - Cube
        CubeListener.clearCube(CubeListener.currentLocation);

        // - Cooldowns
        DynamicCooldownManager.saveCooldowns();

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

    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }
}
