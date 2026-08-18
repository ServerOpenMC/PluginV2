package fr.openmc.core.registry.features;

import com.google.common.base.Supplier;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.admin.freeze.FreezeManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.analytics.AnalyticsManager;
import fr.openmc.core.features.animations.AnimationsManager;
import fr.openmc.core.features.bits.BitsManager;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.displays.TabList;
import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.contents.HelpConfigManager;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.economy.BankManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.features.events.EventsManager;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.halloween.managers.HalloweenManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.homes.icons.HomeIconCacheManager;
import fr.openmc.core.features.itemsadder.elevator.ElevatorManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import fr.openmc.core.features.privatemessage.SocialSpyManager;
import fr.openmc.core.features.profile.ProfileManager;
import fr.openmc.core.features.quests.QuestProgressSaveManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.shops.managers.ShopManager;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.features.toor.DiscordLinkManager;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.updates.UpdateManager;
import fr.openmc.core.hooks.*;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;
import fr.openmc.core.registry.features.loading.FeatureEntry;
import fr.openmc.core.registry.features.loading.FeatureFlag;
import fr.openmc.core.registry.features.loading.FeatureLoadingType;
import fr.openmc.core.utils.text.MotdUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeaturesRegistry extends Registry<String, Feature>
        implements KeyedRegistry<String, Feature> {

    // * Flags pré enregistrés
    public final FeatureFlag NOT_IN_UNIT_TEST = new FeatureFlag.NotInUnitTest();
    public final FeatureFlag NEED_FANCY_NPC = new FeatureFlag.NeedApi(FancyNpcsHook::isEnable, "FancyNPC");
    public final FeatureFlag NEED_LUCK_PERMS = new FeatureFlag.NeedApi(LuckPermsHook::isEnable, "LuckPerms");
    public final FeatureFlag NEED_ITEMS_ADDER = new FeatureFlag.NeedApi(ItemsAdderHook::isEnable, "ItemsAdder");
    public final FeatureFlag NEED_PAPI = new FeatureFlag.NeedApi(PapiHook::isEnable, "PlaceHolderAPI");
    public final FeatureFlag NEED_PROTOCOL_LIB = new FeatureFlag.NeedApi(ProtocolLibHook::isEnable, "ProtocolLib");
    public final FeatureFlag NEED_WORLD_GUARD = new FeatureFlag.NeedApi(WorldGuardHook::isEnable, "WorldGuard");

    private final List<FeatureEntry<?>> declarations = new ArrayList<>();

    public final FeatureEntry<TicketManager> TICKETS = declare(FeatureLoadingType.RUNTIME,
            () -> new TicketManager(new File(OMCPlugin.getInstance().getDataFolder(), "data/stats")));

    public final FeatureEntry<PrivateMessageManager> PRIVATE_SPY = declare(FeatureLoadingType.RUNTIME,
            PrivateMessageManager::new);
    public final FeatureEntry<SocialSpyManager> SOCIAL_SPY = declare(FeatureLoadingType.RUNTIME,
            SocialSpyManager::new);
    public final FeatureEntry<SpawnManager> SPAWN = declare(FeatureLoadingType.RUNTIME,
            SpawnManager::new);
    public final FeatureEntry<UpdateManager> UPDATE = declare(FeatureLoadingType.RUNTIME,
            UpdateManager::new);
    public final FeatureEntry<EconomyManager> ECONOMY = declare(FeatureLoadingType.RUNTIME,
            EconomyManager::new);
    public final FeatureEntry<BankManager> BANK = declare(FeatureLoadingType.RUNTIME,
            BankManager::new);
    public final FeatureEntry<BitsManager> BITS = declare(FeatureLoadingType.RUNTIME,
            BitsManager::new);
    public final FeatureEntry<ScoreboardManager> SCOREBOARD = declare(FeatureLoadingType.RUNTIME,
            () -> new ScoreboardManager(), NOT_IN_UNIT_TEST, NEED_LUCK_PERMS);
    public final FeatureEntry<HomesManager> HOMES = declare(FeatureLoadingType.RUNTIME,
            HomesManager::new);
    public final FeatureEntry<TPAManager> TPA = declare(FeatureLoadingType.RUNTIME,
            TPAManager::new);
    public final FeatureEntry<FreezeManager> FREEZE = declare(FeatureLoadingType.RUNTIME,
            FreezeManager::new);
    public final FeatureEntry<TransactionsManager> TRANSACTIONS = declare(FeatureLoadingType.RUNTIME,
            TransactionsManager::new);
    public final FeatureEntry<AnalyticsManager> ANALYTICS = declare(FeatureLoadingType.RUNTIME,
            AnalyticsManager::new);
    public final FeatureEntry<FriendManager> FRIENDS = declare(FeatureLoadingType.RUNTIME,
            FriendManager::new);
    public final FeatureEntry<TabList> TAB = declare(FeatureLoadingType.RUNTIME,
            () -> new TabList(), NOT_IN_UNIT_TEST, NEED_PROTOCOL_LIB);
    public final FeatureEntry<AdminShopManager> ADMIN_SHOP = declare(FeatureLoadingType.RUNTIME,
            AdminShopManager::new);
    public final FeatureEntry<HelpConfigManager> HELP_CONFIG = declare(FeatureLoadingType.RUNTIME,
            HelpConfigManager::new);
    public final FeatureEntry<AnimationsManager> ANIMATIONS = declare(FeatureLoadingType.RUNTIME,
            () -> new AnimationsManager(), NOT_IN_UNIT_TEST, NEED_ITEMS_ADDER);
    public final FeatureEntry<HalloweenManager> HALLOWEEN = declare(FeatureLoadingType.RUNTIME,
            HalloweenManager::new);
    public final FeatureEntry<QuestProgressSaveManager> QUEST_PROGRESS = declare(FeatureLoadingType.RUNTIME,
            QuestProgressSaveManager::new);
    public final FeatureEntry<MotdUtils> MOTD = declare(FeatureLoadingType.RUNTIME,
            MotdUtils::new);
    public final FeatureEntry<MascotsManager> MASCOTS = declare(FeatureLoadingType.RUNTIME,
            MascotsManager::new);
    public final FeatureEntry<PlayerSettingsManager> PLAYER_SETTINGS = declare(FeatureLoadingType.RUNTIME,
            PlayerSettingsManager::new);
    public final FeatureEntry<MailboxManager> MAILBOX = declare(FeatureLoadingType.RUNTIME,
            MailboxManager::new);
    public final FeatureEntry<DiscordLinkManager> DISCORD_LINK = declare(FeatureLoadingType.RUNTIME,
            DiscordLinkManager::new);
    public final FeatureEntry<ElevatorManager> ELEVATOR = declare(FeatureLoadingType.RUNTIME,
            () -> new ElevatorManager(), NEED_ITEMS_ADDER);
    public final FeatureEntry<ProfileManager> PROFILE = declare(FeatureLoadingType.AFTER_IA,
            ProfileManager::new);
    public final FeatureEntry<QuestsManager> QUESTS = declare(FeatureLoadingType.AFTER_IA,
            QuestsManager::new);
    public final FeatureEntry<CityManager> CITY = declare(FeatureLoadingType.AFTER_IA,
            CityManager::new);
    public final FeatureEntry<DynamicCooldownManager> DYNAMIC_COOLDOWN = declare(FeatureLoadingType.AFTER_IA,
            DynamicCooldownManager::new);
    public final FeatureEntry<ContestManager> CONTEST = declare(FeatureLoadingType.AFTER_IA,
            ContestManager::new);
    public final FeatureEntry<WeeklyEventsManager> WEEKLY_EVENTS = declare(FeatureLoadingType.AFTER_IA,
            WeeklyEventsManager::new);
    public final FeatureEntry<DailyEventsManager> DAILY_EVENTS = declare(FeatureLoadingType.AFTER_IA,
            DailyEventsManager::new);
    public final FeatureEntry<ChatAnimationManager> CHAT_ANIMATIONS = declare(FeatureLoadingType.AFTER_IA,
            ChatAnimationManager::new);
    public final FeatureEntry<EventsManager> EVENTS = declare(FeatureLoadingType.AFTER_IA,
            EventsManager::new);
    public final FeatureEntry<DreamManager> DREAM = declare(FeatureLoadingType.AFTER_IA,
            DreamManager::new);
    public final FeatureEntry<MultiBlockManager> MULTIBLOCKS = declare(FeatureLoadingType.AFTER_IA,
            () -> new MultiBlockManager(), NOT_IN_UNIT_TEST);
    public final FeatureEntry<MilestonesManager> MILESTONES = declare(FeatureLoadingType.AFTER_IA,
            MilestonesManager::new);
    public final FeatureEntry<LeaderboardManager> LEADERBOARD = declare(FeatureLoadingType.AFTER_IA,
            () -> new LeaderboardManager(), NOT_IN_UNIT_TEST);
    public final FeatureEntry<MainMenu> MAIN_MENU = declare(FeatureLoadingType.AFTER_IA,
            () -> new MainMenu(), NOT_IN_UNIT_TEST, NEED_PROTOCOL_LIB);
    public final FeatureEntry<HologramLoader> HOLOGRAM_LOADER = declare(FeatureLoadingType.AFTER_IA,
            () -> new HologramLoader(), NOT_IN_UNIT_TEST);
    public final FeatureEntry<BossbarManager> BOSSBAR = declare(FeatureLoadingType.AFTER_IA,
            BossbarManager::new);
    public final FeatureEntry<ShopManager> SHOP = declare(FeatureLoadingType.AFTER_IA,
            ShopManager::new);
    public final FeatureEntry<HomeIconCacheManager> HOME_ICON_CACHE = declare(FeatureLoadingType.AFTER_IA,
            HomeIconCacheManager::new);
    public final FeatureEntry<DimensionOpenerManager> DIMENSION_OPENER = declare(FeatureLoadingType.AFTER_IA,
            DimensionOpenerManager::new);

    private <V extends Feature> FeatureEntry<V> declare(FeatureLoadingType type, Supplier<V> supplier, FeatureFlag... flags) {
        FeatureEntry<V> entry = new FeatureEntry<>(supplier, type, flags);
        declarations.add(entry);
        return entry;
    }

    @Override
    public void init() {
        load(FeatureLoadingType.RUNTIME);
    }

    @Override
    public void postInit() {
        load(FeatureLoadingType.AFTER_IA);
    }

    @Override
    public void stop() {
        for (Feature feature : values()) {
            feature.startSave();
        }
    }

    public Optional<Feature> register(FeatureEntry<?> entry) {
        if (!entry.shouldLoad()) return Optional.empty();

        Feature feature = entry.create();
        feature.startInit();
        register(key(feature), feature);
        return Optional.of(feature);
    }

    public void load(FeatureLoadingType loadingType) {
        for (FeatureEntry<?> entry : declarations) {
            if (entry.getLoadingType() != loadingType) continue;
            register(entry);
        }
    }

    @Override
    public String key(Feature registryObject) {
        return registryObject.getClass().getSimpleName();
    }
}
