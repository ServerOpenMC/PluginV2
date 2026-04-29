package fr.openmc.core;

import com.j256.ormlite.logger.LoggerFactory;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.packetmenulib.PacketMenuLib;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.hooks.Hooks;
import fr.openmc.core.bootstrap.integration.DatabaseManager;
import fr.openmc.core.bootstrap.integration.ErrorReporter;
import fr.openmc.core.commands.admin.freeze.FreezeManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.analytics.AnalyticsManager;
import fr.openmc.core.features.animations.AnimationsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
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
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.friend.FriendSQLManager;
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
import fr.openmc.core.hooks.*;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.MotdUtils;
import fr.openmc.core.utils.text.TranslationManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin principal OpenMC.
 * Gère le cycle de vie, les features et les hooks globaux.
 */
public class OMCPlugin extends JavaPlugin {
    @Getter
    static OMCPlugin instance;
    @Getter
    static FileConfiguration configs;

    public static final String VANISH_META_KEY = "omcstaff.vanished";

    // ** Registry of OMC Features
    public final List<Feature> REGISTRY_FEATURE = new ArrayList<>(List.of(
            new TicketManager(new File(this.getDataFolder(), "data/stats")),
            new SpawnManager(),
            new UpdateManager(),
            new EconomyManager(),
            new BankManager(),
            new ScoreboardManager(),
            new HomesManager(),
            new TPAQueue(),
            new FreezeManager(),
            new TransactionsManager(),
            new AnalyticsManager(),
            new FriendSQLManager(),
            new TabList(),
            new AdminShopManager(),
            new HelpConfigManager(),
            new BossbarManager(),
            new AnimationsManager(),
            new HalloweenManager(),
            new QuestProgressSaveManager(),
            new MotdUtils(),
            new TranslationManager(new File(this.getDataFolder(), "translations"), "fr"),
            new DynamicCooldownManager(),
            new MascotsManager(),
            new PlayerSettingsManager(),
            new MailboxManager(),
            new QuestsManager(),
            new CityManager(),
            new ContestManager(),
            new WeeklyEventsManager(),
            new DreamManager(),
            new MultiBlockManager(),
            new MilestonesManager(),
            new LeaderboardManager(),
            new MainMenu(),
            new HologramLoader(),
            new HomeIconCacheManager()
    ));

    // ** Registry of OMC Plugin Hooks
    public final List<Hooks> REGISTRY_HOOKS = new ArrayList<>(List.of(
            new ProtocolLibHook(),
            new LuckPermsHook(),
            new PapiHook(),
            new WorldGuardHook(),
            new ItemsAdderHook(),
            new FancyNpcsHook()
    ));

    /**
     * Désactive les logs de ORMLite venant de TableUtils
     */
    @Override
    public void onLoad() {
        LoggerFactory.setLogBackendFactory(DatabaseManager.ShutUpOrmLite::new);
    }

    /**
     * Initialise la configuration, les hooks, les managers et les features.
     */
    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);

        /* HOOKS */
        REGISTRY_HOOKS.forEach(Hooks::startInit);

        if (!OMCPlugin.isUnitTestVersion() && ProtocolLibHook.isEnable())
            PacketMenuLib.init(this);

        logLoadMessage();
        if (!OMCPlugin.isUnitTestVersion()) {
            Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/omc");
            if (pack != null) {
                if (pack.isEnabled()) {
                    logSuccessMessage("Lancement du datapack réussi");
                } else {
                    logErrorMessage("Lancement du datapack échoué");
                }
            }
        }
        new ErrorReporter();

        /* MANAGERS */
        DatabaseManager.init();
        CommandsManager.init();
        ListenersManager.init();

        /* FEATURES */
        REGISTRY_FEATURE.stream()
                .filter(f -> !(f instanceof LoadAfterItemsAdder))
                .forEachOrdered(Feature::startInit);

        // * Si ItemsAdder est pas présent, alors on charge les dernieres features maintenant
        if (!ItemsAdderHook.isEnable()) {
            loadAfterItemsAdder();
        }
    }

    /**
     * Charge les registres et features qui doivent être lancé apres ItemsAdder
     */
    public void loadAfterItemsAdder() {
        // ** REGISTRIES **
        CustomItemRegistry.init();
        CustomEnchantmentRegistry.postInit();
        CustomLootTableRegistry.init();

        // ** FEATURES **
        REGISTRY_FEATURE.stream()
                .filter(f -> f instanceof LoadAfterItemsAdder)
                .forEachOrdered(Feature::startInit);

        // todo: sera supprimé dans https://github.com/ServerOpenMC/PluginV2/pull/1168
        DreamDimensionManager.postInit();

        if (WorldGuardHook.isEnable()) {
            ParticleUtils.spawnParticlesInRegion("spawn", Bukkit.getWorld("world"), Particle.CHERRY_LEAVES, 50, 70, 130);
        }
    }

    /**
     * Sauvegarde l'état des features
     */
    @Override
    public void onDisable() {
        // ** SAVE **
        for (Feature feature : REGISTRY_FEATURE) {
            feature.startSave();
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

    /**
     * Enregistre une liste de listeners Bukkit sur l'instance du plugin.
     *
     * @param listeners Listeners à enregistrer
     */
    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    /**
     * Indique si le plugin tourne dans les tests unitaires.
     *
     * @return True si l'instance serveur correspond à MockBukkit
     */
    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }

    /* LOG MESSAGE */
    /**
     * Log un message de succès formate.
     *
     * @param message Message à loguer
     */
    public void logSuccessMessage(String message) {
        this.getSLF4JLogger().info("\u001B[32m✔ {}\u001B[0m", message);
    }

    /**
     * Log un message d'erreur formate.
     *
     * @param message Message à loguer
     */
    public void logErrorMessage(String message) {
        this.getSLF4JLogger().info("\u001B[31m✘ {}\u001B[0m", message);
    }

    /**
     * Affiche la bannière de demarrage et l'état des dependances.
     */
    private void logLoadMessage() {
        Logger log = this.getSLF4JLogger();

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

    /**
     * Log l'état d'une dépendance (requise ou optionnelle).
     *
     * @param name Nom du plugin dépendance
     * @param optional True si la dépendance est optionnelle
     */
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
