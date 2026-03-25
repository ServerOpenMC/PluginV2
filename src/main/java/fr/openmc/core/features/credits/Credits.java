package fr.openmc.core.features.credits;

import fr.openmc.core.registry.items.CustomItemRegistry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@Getter
public enum Credits {
    ADMINSHOP(Material.GOLD_INGOT, "Adminshop", Set.of("Axeno"), Set.of("Gexary")),
    ANIMATIONS(Material.AMETHYST_BLOCK, "Les Animations", Set.of("iambibi_", "gab400"), Set.of("Tfloa")),
    CUBE(Material.LAPIS_BLOCK, "Le Cube", Set.of("iambibi_")),
    CITY(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_chateau").getBest(), "Les Villes", Set.of("iambibi_", "Gyro", "gab400", "Nocolm", "Axeno", "PuppyTransGirl"), Set.of("Tfloa", "Gexary")),
    DREAM(Material.SCULK, "La Dimension des Rêves", Set.of("iambibi_"), Set.of("Tfloa"), Set.of("Mcross_bow")),
    MASCOTS(Material.ZOMBIE_SPAWN_EGG, "Les Mascottes", Set.of("Nocolm")),
    MAYOR(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_bank").getBest(), "Les Maires", Set.of("iambibi_"), Set.of("Gexary")),
    CITY_MILESTONE(Material.NETHER_STAR, "Le Milestone des Villes", Set.of("iambibi_")),
    WAR(Material.IRON_SWORD, "Les Guerres", Set.of("iambibi_")),
    NOTATION(Material.PAPER, "Les Notations", Set.of("iambibi_")),
    RANK(Material.VAULT, "Les Grades", Set.of("gab400")),
    CONTEST(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), "Les Contests", Set.of("iambibi_"), Set.of("Gexary", "Tfloa")),
    HOLOGRAMS(Material.OAK_HANGING_SIGN, "Les Hologrammes", Set.of("iambibi_", "miseur")),
    ECONOMY(Material.GOLD_BLOCK, "L'Economie", Set.of("Axeno", "Piquel Chips", "PuppyTransGirl", "Gyro")),
    FRIENDS(Material.EMERALD_BLOCK, "Le systeme d'ami", Set.of("Axeno")),
    HOMES(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_maison").getBest(), "Le Systeme d'Home", Set.of("Axeno"), Set.of("Gexary")),
    LEADERBOARD(Material.ANCIENT_DEBRIS, "Les Classements", Set.of("miseur")),
    MAILBOX(Material.PAPER, "La Boite aux Lettres", Set.of("Gexary", "Axeno"), Set.of("Gexary")),
    MAINMENU(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_information").getBest(), "Le Menu Principal", Set.of("miseur"), Set.of("Tfloa")),
    MILESTONES(Material.SEA_LANTERN, "Les Milestones", Set.of("iambibi_", "gab400")),
    PRIVATEMESSAGE(Material.ZOMBIE_HEAD, "Les messages privés", Set.of("Axeno")),
    QUEST(CustomItemRegistry.getByName("omc_homes:omc_homes_icon_chateau").getBest(), "Les Quêtes", Set.of("Axeno"), Set.of("Gexary")),
    SETTINGS(Material.REDSTONE_TORCH, "Les Paramêtres", Set.of("Axeno"), Set.of("Gexary")),
    TICKETS(Material.BOOK, "Les Tickets V1", Set.of("Axeno"), Set.of("Tfloa")),
    TPA(Material.ENDER_PEARL, "Le Tpa", Set.of("gab400")),
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
}
