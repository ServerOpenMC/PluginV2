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
import fr.openmc.core.bootstrap.integration.OMCLogger;
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
import fr.openmc.core.features.events.commands.calendar.CalendarManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import fr.openmc.core.features.privatemessage.SocialSpyManager;
import fr.openmc.core.features.quests.QuestProgressSaveManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.hooks.*;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.loottable.CustomLootTableRegistry;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import fr.openmc.core.utils.text.MotdUtils;
import io.papermc.paper.datapack.Datapack;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
            new PrivateMessageManager(),
            new SocialSpyManager(),
            new SpawnManager(),
            new UpdateManager(),
            new EconomyManager(),
            new BankManager(),
            new ScoreboardManager(),
            new HomesManager(),
            new TPAManager(),
            new FreezeManager(),
            new TransactionsManager(),
            new AnalyticsManager(),
            new FriendManager(),
            new TabList(),
            new AdminShopManager(),
            new HelpConfigManager(),
            new BossbarManager(),
            new AnimationsManager(),
            new HalloweenManager(),
            new QuestProgressSaveManager(),
            new MotdUtils(),
            new MascotsManager(),
            new PlayerSettingsManager(),
            new MailboxManager(),
            new QuestsManager(),
            new CityManager(),
            new DynamicCooldownManager(),
            new ContestManager(),
            new WeeklyEventsManager(),
            new CalendarManager(),
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
        OMCLogger.setRuntimeLogger(this.getSLF4JLogger());

        /* EXTERNALS */
        MenuLib.init(this);

        /* HOOKS */
        REGISTRY_HOOKS.forEach(Hooks::startInit);

        if (!OMCPlugin.isUnitTestVersion() && ProtocolLibHook.isEnable())
            PacketMenuLib.init(this);

        OMCLogger.logLoadMessage(this);
        if (!OMCPlugin.isUnitTestVersion()) {
            Datapack pack = this.getServer().getDatapackManager().getPack(getPluginMeta().getName() + "/omc");
            if (pack != null) {
                if (pack.isEnabled()) {
                    OMCLogger.successFormatted("Lancement du datapack réussi");
                } else {
                    OMCLogger.error("Lancement du datapack échoué");
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
        // ** LOAD ITEMS ADDER CONTENTS **
        ItemsAdderHook.loadContents();

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
}
