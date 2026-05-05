package fr.openmc.core.features.credits;

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
import fr.openmc.core.registry.items.CustomItemRegistry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum Credits {
    ADMINSHOP(Material.GOLD_INGOT, "Adminshop", AdminShopManager.class),
    ANIMATIONS(Material.AMETHYST_BLOCK, "Les Animations", AnimationsManager.class),
    CUBE(Material.LAPIS_BLOCK, "Le Cube", Set.of("iambibi_")),
    CITY(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_chateau").getBest(), "Les Villes", CityManager.class),
    DREAM(Material.SCULK, "La Dimension des Rêves", DreamManager.class),
    DREAM_MILESTONE(CustomItemRegistry.getByName("omc_dream:singularity").getBest(), "Le Milestone des Rêves", Set.of("gab400", "Rylo42 (histoire et dialogues)")),
    MASCOTS(Material.ZOMBIE_SPAWN_EGG, "Les Mascottes", MascotsManager.class),
    MAYOR(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bank").getBest(), "Les Maires", Set.of("iambibi_"), Set.of("Gexary")),
    CITY_MILESTONE(Material.NETHER_STAR, "Le Milestone des Villes", Set.of("iambibi_")),
    WAR(Material.IRON_SWORD, "Les Guerres", Set.of("iambibi_")),
    NOTATION(Material.PAPER, "Les Notations", Set.of("iambibi_")),
    RANK(Material.VAULT, "Les Grades", Set.of("gab400")),
    CONTEST(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), "Les Contests", ContestManager.class),
    WEEKLY_EVENTS(Material.FIREWORK_ROCKET, "Les Evenements Hebdomadaires", WeeklyEventsManager.class),
    HOLOGRAMS(Material.OAK_HANGING_SIGN, "Les Hologrammes", HologramLoader.class),
    ECONOMY(Material.GOLD_BLOCK, "L'Economie", EconomyManager.class),
    FRIENDS(Material.EMERALD_BLOCK, "Le systeme d'ami", FriendManager.class),
    HOMES(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_maison").getBest(), "Le Systeme d'Home", HomesManager.class),
    LEADERBOARD(Material.ANCIENT_DEBRIS, "Les Classements", LeaderboardManager.class),
    MAILBOX(Material.PAPER, "La Boite aux Lettres", MailboxManager.class),
    MAINMENU(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_information").getBest(), "Le Menu Principal", MainMenu.class),
    MILESTONES(Material.SEA_LANTERN, "Les Milestones", MilestonesManager.class),
    PRIVATEMESSAGE(Material.ZOMBIE_HEAD, "Les messages privés", PrivateMessageManager.class),
    QUEST(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_chateau").getBest(), "Les Quêtes", QuestsManager.class),
    SETTINGS(Material.REDSTONE_TORCH, "Les Paramêtres", PlayerSettingsManager.class),
    TICKETS(Material.BOOK, "Les Tickets V1", TicketManager.class),
    TPA(Material.ENDER_PEARL, "Le Tpa", TPAManager.class),
    RTP(Material.ENDER_PEARL, "Le RTP", Set.of("miseur")),
    VERSIONNING(Material.COMMAND_BLOCK_MINECART, "Le Versionning", Set.of("Piquel Chips")),
    CUSTOMITEMS(Material.COMMAND_BLOCK, "Les Custom Items", Set.of("Axeno")),
    CHRONOMETER(Material.COMMAND_BLOCK, "Chronomêtre", Set.of("Nocolm")),
    COOLDOWN(Material.COMMAND_BLOCK, "Cooldown", Set.of("Gyro", "iambibi_")),
    MENU_LIB(Material.COMMAND_BLOCK, "Systeme de Menu", Set.of("Xernas78", "PuppyTransGirl", "iambibi_", "gab400")),
    PACKET_MENU_LIB(Material.COMMAND_BLOCK, "Systeme de Menu en Packet", Set.of("miseur")),
    ERRORHANDLER(Material.COMMAND_BLOCK, "Le systeme de gestion d'erreur", Set.of("iambibi_")),
    UNITTEST(Material.COMMAND_BLOCK, "Les tests unitaires", Set.of("Nirbose", "Gyro")),
    ORM(Material.COMMAND_BLOCK, "Systeme de base de données", Set.of("Piquel Chips")),
    ;

    private final ItemStack icon;
    private final String featureName;
    private final Set<String> developpers;
    private final Set<String> graphists;
    private final Set<String> builders;

    /**
     * Constructeur qui récupère automatiquement les contributeurs de la classe Feature via annotation.
     * @param icon Icône du crédit
     * @param featureName Nom de la feature
     * @param featureClass Classe Feature (ex: AdminShopManager.class)
     */
    Credits(Material icon, String featureName, Class<? extends Feature> featureClass) {
        this.icon = ItemStack.of(icon);
        this.featureName = featureName;
        var contributors = getContributorsFromClass(featureClass);
        this.developpers = contributors[0];
        this.graphists = contributors[1];
        this.builders = contributors[2];
    }

    /**
     * Constructeur qui récupère automatiquement les contributeurs de la classe Feature via annotation.
     * @param icon Icône du crédit (ItemStack)
     * @param featureName Nom de la feature
     * @param featureClass Classe Feature (ex: AdminShopManager.class)
     */
    Credits(ItemStack icon, String featureName, Class<? extends Feature> featureClass) {
        this.icon = icon;
        this.featureName = featureName;
        var contributors = getContributorsFromClass(featureClass);
        this.developpers = contributors[0];
        this.graphists = contributors[1];
        this.builders = contributors[2];
    }

    Credits(Material icon, String featureName, Set<String> developpers) {
        this.icon = ItemStack.of(icon);
        this.developpers = developpers;
        this.featureName = featureName;
        this.graphists = Set.of();
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureName, Set<String> developpers) {
        this.icon = icon;
        this.developpers = developpers;
        this.featureName = featureName;
        this.graphists = Set.of();
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureName, Set<String> developpers, Set<String> graphists) {
        this.icon = icon;
        this.featureName = featureName;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = Set.of();
    }

    Credits(Material icon, String featureName, Set<String> developpers, Set<String> graphists) {
        this.icon = ItemStack.of(icon);
        this.featureName = featureName;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = Set.of();
    }

    Credits(ItemStack icon, String featureName, Set<String> developpers, Set<String> graphists, Set<String> builders) {
        this.icon = icon;
        this.featureName = featureName;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = builders;
    }

    Credits(Material icon, String featureName, Set<String> developpers, Set<String> graphists, Set<String> builders) {
        this.icon = ItemStack.of(icon);
        this.featureName = featureName;
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
