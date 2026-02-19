package fr.openmc.core;

import com.j256.ormlite.logger.LoggerFactory;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.hooks.*;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.commands.admin.freeze.FreezeManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.animations.AnimationsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.displays.TabList;
import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.contents.HelpConfigManager;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.QuestProgressSaveManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.items.usable.CustomUsableItemRegistry;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.ParticleUtils;
import fr.openmc.core.utils.ShutUpOrmLite;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.errors.ErrorReporter;
import fr.openmc.core.utils.translation.TranslationManager;
import io.papermc.paper.datapack.Datapack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;

public class OMCPlugin extends JavaPlugin {
    @Getter
    static OMCPlugin instance;
    @Getter
    static FileConfiguration configs;

    public static final String VANISH_META_KEY = "omcstaff.vanished";

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }

    @Override
    public void onLoad() {
        LoggerFactory.setLogBackendFactory(ShutUpOrmLite::new);
    }

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);

        LuckPermsHook.init();
        PapiHook.init();
        WorldGuardHook.init();
        ItemsAdderHook.init();
        FancyNpcsHook.init();
        if (!OMCPlugin.isUnitTestVersion())
            PacketMenuLib.init(this);

        logLoadMessage();
        if (!OMCPlugin.isUnitTestVersion()) {
            Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/omc");
            if (pack != null) {
                if (pack.isEnabled()) {
                    getSLF4JLogger().info("\u001B[32m✔ Lancement du datapack réussi\u001B[0m");
                } else {
                    getSLF4JLogger().warn("\u001B[31m✘ Lancement du datapack échoué\u001B[0m");
                }
            }
        }
        new ErrorReporter();

        /* MANAGERS */
        TicketManager.loadPlayerStats(new File(this.getDataFolder(), "data/stats"));
        DatabaseManager.init();
        CommandsManager.init();
        SpawnManager.init();
        UpdateManager.init();
        ListenersManager.init();
        EconomyManager.init();
        BankManager.init();

        if (!isUnitTestVersion())
            ScoreboardManager.init();

        HomesManager.init();
        TPAQueue.initCommand();
        FreezeManager.init();
        QuestProgressSaveManager.init();
        TabList.init();
        AdminShopManager.init();
        HelpConfigManager.init();
        BossbarManager.init();
        AnimationsManager.init();
        HalloweenManager.init();

        MotdUtils.init();
        TranslationManager.init(new File(this.getDataFolder(), "translations"), "fr");
        DynamicCooldownManager.init();

        MascotsManager.init();

        PlayerSettingsManager.loadAllPlayerSettings();

        MailboxManager.loadLetters();
    }

    public void loadWithItemsAdder() {
        CustomItemRegistry.init();
        CustomUsableItemRegistry.init();
	    MilestonesManager.init();
	    QuestsManager.init();
        CityManager.init();
        ContestManager.init();
        DreamManager.init();
        MultiBlockManager.init();
        if (WorldGuardHook.isHasWorldGuard()) {
            ParticleUtils.spawnParticlesInRegion("spawn", Bukkit.getWorld("world"), Particle.CHERRY_LEAVES, 50, 70, 130);
            ParticleUtils.spawnContestParticlesInRegion("spawn", Bukkit.getWorld("world"), 10, 70, 135);
        }

        DreamDimensionManager.postInit();
        if (!OMCPlugin.isUnitTestVersion()) {
            LeaderboardManager.init();
            MainMenu.init(this);
            HologramLoader.init();
        }
        HomeIconCacheManager.initialize();
    }

    @Override
    public void onDisable() {
        // SAUVEGARDE
        // - Dimension des Reves
        DreamManager.disable();

        // - Mailboxes
        MailboxManager.saveLetters();

        // - MultiBlocks
        MultiBlockManager.save();

        // - War
        WarManager.saveWarHistories();

        // - CityStatistics
        CityStatisticsManager.saveCityStatistics();

        // - Settings
        PlayerSettingsManager.saveAllSettings();

        // - Notation des Villes
        NotationManager.saveNotations();

        // - Maires
        MayorManager.saveMayorConstant();
        MayorManager.savePlayersVote();
        MayorManager.saveMayorCandidates();
        MayorManager.saveCityMayors();
        MayorManager.saveCityLaws();

        HomesManager.saveHomesData();
        HomeIconCacheManager.clearCache();

        // - Milestones
        MilestonesManager.saveMilestonesData();

        // - Contest
        ContestManager.saveContestData();
        ContestManager.saveContestPlayerData();
        QuestsManager.saveQuests();

        // - Mascottes
        MascotsManager.saveMascots();

        // - Cooldowns
        DynamicCooldownManager.saveCooldowns();


        if (!OMCPlugin.isUnitTestVersion()) {
            HologramLoader.unloadAll();
        }

        // - Close all inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }

        // If the plugin crashes, shutdown the server
        if (!isUnitTestVersion())
            if (!Bukkit.isStopping())
                Bukkit.shutdown();
    }

    private void logLoadMessage() {
        Logger log = getSLF4JLogger();

        String pluginVersion = getPluginMeta().getVersion();
        String javaVersion = System.getProperty("java.version");
        String server = Bukkit.getName() + " " + Bukkit.getVersion();

        log.info("\u001B[1;35m   ____    _____   ______   _   _   __  __   _____       \u001B[0;90mOpenMC {}\u001B[0m", pluginVersion);
        log.info("\u001B[1;35m  / __ \\  |  __ \\ |  ____| | \\ | | |  \\/  | / ____|      \u001B[0;90m{}\u001B[0m", server);
        log.info("\u001B[1;35m | |  | | | |__) || |__    |  \\| | | \\  / || |           \u001B[0;90mJava {}\u001B[0m", javaVersion);
        log.info("\u001B[1;35m | |  | | |  ___/ |  __|   | . ` | | |\\/| || |          \u001B[0m");
        log.info("\u001B[1;35m | |__| | | |     | |____  | |\\  | | |  | || |____      \u001B[0m");
        log.info("\u001B[1;35m  \\____/  |_|     |______| |_| \\_| |_|  |_| \\_____|   \u001B[0m");
        log.info("");

        for (String requiredPlugins : getPluginMeta().getPluginDependencies()) {
            logPluginStatus(requiredPlugins, false);
        }

        for (String optionalPlugins : getPluginMeta().getPluginSoftDependencies()) {
            logPluginStatus(optionalPlugins, true);
        }
    }

    private void logPluginStatus(String name, boolean optional) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        boolean enabled = plugin != null && plugin.isEnabled();

        String icon = enabled ? "✔" : "✘";
        String color = enabled ? "\u001B[32m" : "\u001B[31m";
        String version = enabled ? " v" + plugin.getPluginMeta().getVersion() : "";
        String label = optional ? " (facultatif)" : "";

        getSLF4JLogger().info("  {}{} {}{}{}\u001B[0m", color, icon, name, version, label);
    }
}
