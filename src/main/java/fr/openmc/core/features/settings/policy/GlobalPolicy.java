package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum GlobalPolicy {
    EVERYONE("§aTout le monde", "Visible par tous les joueurs"),
    FRIENDS("§dAmis uniquement", "Visible uniquement par mes amis"),
    CITY_MEMBERS("§6Membres de ma ville", "Visible par les membres de ma ville"),
    NOBODY("§cPersonne", "Information cachée à tous");

    private final String displayName;
    private final String description;

    GlobalPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}