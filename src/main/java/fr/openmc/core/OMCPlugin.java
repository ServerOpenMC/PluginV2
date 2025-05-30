package fr.openmc.core;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.core.commands.admin.freeze.FreezeManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.accountdetection.AccountDetectionManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.bossbar.BossbarManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.corporation.manager.CompanyManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.quests.QuestProgressSaveManager;
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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;

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

        logLoadMessage();

        /* MANAGERS */
        new DatabaseManager();
        new CommandsManager();
        new CustomItemRegistry();
        new SpawnManager();
        new UpdateManager();
        new MascotsManager(); // laisser avant CityManager
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
        new QuestProgressSaveManager();
        new TabList();
        new AdminShopManager();
        new AccountDetectionManager();
        new BossbarManager();
        new CompanyManager();// laisser apres Economy Manager
        new ContestManager();

        if (!OMCPlugin.isUnitTestVersion())
            new LeaderboardManager();

        new MotdUtils();
        new TranslationManager(new File(this.getDataFolder(), "translations"), "fr");
        new DynamicCooldownManager();

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

        // - Companies & Shop
        CompanyManager.saveAllCompanies();
        CompanyManager.saveAllShop();

        HomesManager.saveHomesData();

        // - Contest
        ContestManager.saveContestData();
        ContestManager.saveContestPlayerData();
        QuestsManager.saveQuests();

        // - Mascottes
        MascotsManager.saveMascots();

        // - Cube
        CubeListener.clearCube(CubeListener.currentLocation);

        // - Cooldowns
        DynamicCooldownManager.saveCooldowns();

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

    private void logLoadMessage() {
        Logger log = getLogger();

        String pluginVersion = getDescription().getVersion();
        String javaVersion = System.getProperty("java.version");
        String server = Bukkit.getName() + " " + Bukkit.getVersion();

        log.info("\u001B[1;35m   ____    _____   ______   _   _   __  __   _____       " + "\u001B[0;90mOpenMC " + pluginVersion + "\u001B[0m");
        log.info("\u001B[1;35m  / __ \\  |  __ \\ |  ____| | \\ | | |  \\/  | / ____|      " + "\u001B[0;90m" + server + "\u001B[0m");
        log.info("\u001B[1;35m | |  | | | |__) || |__    |  \\| | | \\  / || |           " + "\u001B[0;90mJava " + javaVersion + "\u001B[0m");
        log.info("\u001B[1;35m | |  | | |  ___/ |  __|   | . ` | | |\\/| || |          \u001B[0m");
        log.info("\u001B[1;35m | |__| | | |     | |____  | |\\  | | |  | || |____      \u001B[0m");
        log.info("\u001B[1;35m  \\____/  |_|     |______| |_| \\_| |_|  |_| \\_____|   \u001B[0m");
        log.info("");

        String[] plugins = {
                "WorldEdit", "WorldGuard", "LuckPerms", "ItemsAdder", "PlaceholderAPI", "FancyNpcs", "ProtocolLib"
        };

        for (String pluginName : plugins) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                log.info("  \u001B[32m✔ " + pluginName + " v" + plugin.getDescription().getVersion() + " trouvé \u001B[0m");
            } else {
                log.info("  \u001B[31m✘ " + pluginName + " (facultatif)\u001B[0m");
            }
        }
    }
}
