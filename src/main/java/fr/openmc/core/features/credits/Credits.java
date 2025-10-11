package fr.openmc.core.features.credits;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Credits {
    ADMINSHOP("Adminshop", Set.of("Axeno"), Set.of("Gexary")),
    ANIMATIONS("Les Animations", Set.of("iambibi_"), Set.of("Tfloa")),
    CUBE("Le Cube", Set.of("iambibi_")),
    CITY("Les Villes", Set.of("iambibi_", "Gryo", "gab400", "Nocolm", "Axeno", "Toutouchien"), Set.of("Tfloa", "Gexary")),
    MASCOTS("Les Mascottes", Set.of("Nocolm")),
    MAYOR("Les Maires", Set.of("iambibi_"), Set.of("Gexary")),
    CITY_MILESTONE("Le Milestone des Villes", Set.of("iambibi_")),
    WAR("Les Guerres", Set.of("iambibi_")),
    NOTATION("Les Notations", Set.of("iambibi_")),
    RANK("Les Grades", Set.of("gab400")),
    CONTEST("Les Contests", Set.of("iambibi_"), Set.of("Gexary", "Tfloa")),
    HOLOGRAMS("Les Hologrammes", Set.of("iambibi_", "miseur")),
    ECONOMY("L'Economie", Set.of("Axeno", "Piquel Chips", "Gyro")),
    FRIENDS("Le Systeme d'Ami", Set.of("Axeno")),
    HOMES("Le Systeme d'Home", Set.of("Axeno"), Set.of("Gexary")),
    LEADERBOARD("Les Classements", Set.of("miseur")),
    MAILBOX("La Boite aux Lettres", Set.of("Gexary"), Set.of("Gexary")),
    MAINMENU("Le Menu Principal", Set.of("miseur"), Set.of("Tfloa")),
    MILESTONES("Les Milestones", Set.of("iambibi_", "gab400")),
    PRIVATEMESSAGE("Les messages privés", Set.of("Axeno")),
    QUEST("Les Quêtes", Set.of("Axeno"), Set.of("Gexary")),
    SETTINGS("Les Paramêtres", Set.of("Axeno"), Set.of("Gexary")),
    TICKETS("Les Tickets V1", Set.of("Axeno"), Set.of("Tfloa")),
    TPA("Le Tpa", Set.of("gab400")),
    VERSIONNING("Le Versionning", Set.of("Piquel Chips")),
    CUSTOMITEMS("Les Custom Items", Set.of("Axeno")),
    CHRONOMETER("Chronomêtre", Set.of("Nocolm")),
    COOLDOWN("Cooldown", Set.of("Gyro", "iambibi_")),
    MENU_LIB("MenuLib", Set.of("Xernas78", "iambibi_", "gab400")),
    PACKET_MENU_LIB("PacketMenuLib", Set.of("miseur")),
    FREEZE_COMMAND("Freeze Command", Set.of("gab400")),
    ERRORHANDLER("Le systeme de gestion d'erreur", Set.of("iambibi_")),
    UNITTEST("Les tests unitaires", Set.of("Nirbose", "Gyro")),
    ORM("ORM", Set.of("Piquel Chips")),
    ;

    private final String featureName;
    private final Set<String> developpers;
    private Set<String> graphists = Set.of();
    private Set<String> builders = Set.of();

    Credits(String featureName, Set<String> developpers) {
        this.developpers = developpers;
        this.featureName = featureName;
    }

    Credits(String featureName, Set<String> developpers, Set<String> graphists) {
        this.featureName = featureName;
        this.developpers = developpers;
        this.graphists = graphists;
    }

    Credits(String featureName, Set<String> developpers, Set<String> graphists, Set<String> builders) {
        this.featureName = featureName;
        this.developpers = developpers;
        this.graphists = graphists;
        this.builders = builders;
    }
}
