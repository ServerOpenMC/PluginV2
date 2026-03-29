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
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.displays.TabList;
import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.events.halloween.managers.HalloweenManager;
import fr.openmc.core.features.friend.FriendSQLManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.utils.MotdUtils;
import fr.openmc.core.utils.ParticleUtils;
import fr.openmc.core.utils.ShutUpOrmLite;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.errors.ErrorReporter;
import fr.openmc.core.utils.init.Feature;
import fr.openmc.core.utils.init.LoadAfterItemsAdder;
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
import java.util.ArrayList;
import java.util.List;

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
            new BossbarManager(),
            new AnimationsManager(),
            new HalloweenManager(),
            new MotdUtils(),
            new TranslationManager(new File(this.getDataFolder(), "translations"), "fr"),
            new DynamicCooldownManager(),
            new MascotsManager(),
            new PlayerSettingsManager(),
            new MailboxManager(),
            new MilestonesManager(),
            new QuestsManager(),
            new CityManager(),
            new ContestManager(),
            new DreamManager(),
            new MultiBlockManager(),
            new LeaderboardManager(),
            new MainMenu(),
            new HologramLoader(),
            new HomeIconCacheManager()
    ));

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
    }

    public void loadWithItemsAdder() {
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

        if (WorldGuardHook.isHasWorldGuard()) {
            ParticleUtils.spawnParticlesInRegion("spawn", Bukkit.getWorld("world"), Particle.CHERRY_LEAVES, 50, 70, 130);
        }
    }

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

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public static boolean isUnitTestVersion() {
        return OMCPlugin.instance.getServer().getVersion().contains("MockBukkit");
    }

    /* LOG MESSAGE */
    public void logSuccessMessage(String message) {
        this.getSLF4JLogger().info("\u001B[32m✔ {}\u001B[0m", message);
    }

    public void logErrorMessage(String message) {
        this.getSLF4JLogger().info("\u001B[31m✔ {}\u001B[0m", message);
    }

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
