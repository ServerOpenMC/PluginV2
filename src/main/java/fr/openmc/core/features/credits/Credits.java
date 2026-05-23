package fr.openmc.core.features.credits;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.animations.AnimationsManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestManager;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.homes.HomesManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mainmenu.MainMenu;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.tickets.TicketManager;
import fr.openmc.core.features.tpa.TPAManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum Credits {
    ADMINSHOP(Material.GOLD_INGOT, "feature.credits.feature.adminshop", AdminShopManager.class),
    ANIMATIONS(Material.AMETHYST_BLOCK, "feature.credits.feature.animations", AnimationsManager.class),
    CUBE(Material.LAPIS_BLOCK, "feature.credits.feature.cube", Set.of("iambibi_")),
    CITY(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_chateau").getBest(), "feature.credits.feature.city", CityManager.class),
    DREAM(Material.SCULK, "feature.credits.feature.dream", DreamManager.class),
    DREAM_MILESTONE(OMCRegistry.CUSTOM_ITEMS.get("omc_dream:singularity").getBest(), "feature.credits.feature.dream_milestone", Set.of("gab400", "Rylo42 (histoire et dialogues)")),
    MASCOTS(Material.ZOMBIE_SPAWN_EGG, "feature.credits.feature.mascots", MascotsManager.class),
    MAYOR(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_bank").getBest(), "feature.credits.feature.mayor", Set.of("iambibi_"), Set.of("Gexary")),
    CITY_MILESTONE(Material.NETHER_STAR, "feature.credits.feature.city_milestone", Set.of("iambibi_")),
    WAR(Material.IRON_SWORD, "feature.credits.feature.war", Set.of("iambibi_")),
    NOTATION(Material.PAPER, "feature.credits.feature.notation", Set.of("iambibi_")),
    RANK(Material.VAULT, "feature.credits.feature.rank", Set.of("gab400")),
    CONTEST(OMCRegistry.CUSTOM_ITEMS.get("omc_contest:contest_shell").getBest(), "feature.credits.feature.contest", ContestManager.class),
    WEEKLY_EVENTS(Material.FIREWORK_ROCKET, "feature.credits.feature.weekly_events", WeeklyEventsManager.class),
    HOLOGRAMS(Material.OAK_HANGING_SIGN, "feature.credits.feature.holograms", HologramLoader.class),
    ECONOMY(Material.GOLD_BLOCK, "feature.credits.feature.economy", EconomyManager.class),
    FRIENDS(Material.EMERALD_BLOCK, "feature.credits.feature.friends", FriendManager.class),
    HOMES(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_maison").getBest(), "feature.credits.feature.homes", HomesManager.class),
    LEADERBOARD(Material.ANCIENT_DEBRIS, "feature.credits.feature.leaderboard", LeaderboardManager.class),
    MAILBOX(Material.PAPER, "feature.credits.feature.mailbox", MailboxManager.class),
    MAINMENU(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_information").getBest(), "feature.credits.feature.mainmenu", MainMenu.class),
    MILESTONES(Material.SEA_LANTERN, "feature.credits.feature.milestones", MilestonesManager.class),
    PRIVATEMESSAGE(Material.ZOMBIE_HEAD, "feature.credits.feature.privatemessage", PrivateMessageManager.class),
    QUEST(OMCRegistry.CUSTOM_ITEMS.get("omc_homes:omc_homes_icon_chateau").getBest(), "feature.credits.feature.quest", QuestsManager.class),
    SETTINGS(Material.REDSTONE_TORCH, "feature.credits.feature.settings", PlayerSettingsManager.class),
    TICKETS(Material.BOOK, "feature.credits.feature.tickets", TicketManager.class),
    TPA(Material.ENDER_PEARL, "feature.credits.feature.tpa", TPAManager.class),
    RTP(Material.ENDER_PEARL, "feature.credits.feature.rtp", Set.of("miseur")),
    VERSIONNING(Material.COMMAND_BLOCK_MINECART, "feature.credits.feature.versionning", Set.of("Piquel Chips")),
    CUSTOMITEMS(Material.COMMAND_BLOCK, "feature.credits.feature.customitems", Set.of("Axeno")),
    CHRONOMETER(Material.COMMAND_BLOCK, "feature.credits.feature.chronometer", Set.of("Nocolm")),
    COOLDOWN(Material.COMMAND_BLOCK, "feature.credits.feature.cooldown", Set.of("Gyro", "iambibi_")),
    MENU_LIB(Material.COMMAND_BLOCK, "feature.credits.feature.menu_lib", Set.of("Xernas78", "PuppyTransGirl", "iambibi_", "gab400")),
    PACKET_MENU_LIB(Material.COMMAND_BLOCK, "feature.credits.feature.packet_menu_lib", Set.of("miseur")),
    ERRORHANDLER(Material.COMMAND_BLOCK, "feature.credits.feature.errorhandler", Set.of("iambibi_")),
    UNITTEST(Material.COMMAND_BLOCK, "feature.credits.feature.unittest", Set.of("Nirbose", "Gyro")),
    ORM(Material.COMMAND_BLOCK, "feature.credits.feature.orm", Set.of("Piquel Chips")),
    ;

    private final ItemStack icon;
    private final String featureKey;
    private final Set<String> developpers;
    private final Set<String> graphists;
    private final Set<String> builders;

    /**
     * Constructeur qui récupère automatiquement les contributeurs de la classe Feature via annotation.
     * @param icon Icône du crédit
     * @param featureKey Translation key de la feature
     * @param featureClass Classe Feature (ex: AdminShopManager.class)
     */
    Credits(Material icon, String featureKey, Class<? extends Feature> featureClass) {
        this.icon = ItemStack.of(icon);
        this.featureKey = featureKey;
        var contributors = getContributorsFromClass(featureClass);
        this.developpers = contributors[0];
        this.graphists = contributors[1];
        this.builders = contributors[2];
    }

    /**
     * Constructeur qui récupère automatiquement les contributeurs de la classe Feature via annotation.
     * @param icon Icône du crédit (ItemStack)
     * @param featureKey La translation key de la feature
     * @param featureClass Classe Feature (ex: AdminShopManager.class)
     */
    Credits(ItemStack icon, String featureKey, Class<? extends Feature> featureClass) {
        this.icon = icon;
        this.featureKey = featureKey;
        var contributors = getContributorsFromClass(featureClass);
        this.developpers = contributors[0];
        this.graphists = contributors[1];
        this.builders = contributors[2];
    }

    Credits(Material icon, String featureKey, Set<String> developpers) {
        this.icon = ItemStack.of(icon);
        this.developpers = developpers;
        this.featureKey = featureKey;
        this.graphists = Set.of();
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureKey, Set<String> developpers) {
        this.icon = icon;
        this.developpers = developpers;
        this.featureKey = featureKey;
        this.graphists = Set.of();
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureKey, Set<String> developpers, Set<String> graphists) {
        this.icon = icon;
        this.featureKey = featureKey;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = Set.of();
    }

    Credits(Material icon, String featureKey, Set<String> developpers, Set<String> graphists) {
        this.icon = ItemStack.of(icon);
        this.featureKey = featureKey;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureKey, Set<String> developpers, Set<String> graphists, Set<String> builders) {
        this.icon = icon;
        this.featureKey = featureKey;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = builders;
    }

    Credits(Material icon, String featureKey, Set<String> developpers, Set<String> graphists, Set<String> builders) {
        this.icon = ItemStack.of(icon);
        this.featureKey = featureKey;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = builders;
    }

    /**
     * Récupère les contributeurs (développeurs, graphistes, constructeurs) à partir de l'annotation @Credits
     * de la classe Feature spécifiée.
     *
     * @param featureClass Classe Feature
     * @return Tableau contenant [Set<développeurs>, Set<graphistes>, Set<constructeurs>]
     */
    private static Set<String>[] getContributorsFromClass(Class<? extends Feature> featureClass) {
        Set<String>[] result = new Set[]{new HashSet<>(), new HashSet<>(), new HashSet<>()};
        
        Credit annotation = featureClass.getAnnotation(Credit.class);
        
        if (annotation != null) {
            result[0] = new HashSet<>(Arrays.asList(annotation.developers()));
            result[1] = new HashSet<>(Arrays.asList(annotation.graphist()));
            result[2] = new HashSet<>(Arrays.asList(annotation.builders()));
        }
        
        return result;
    }
}
