package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum VisibilityLevel {
    NOBODY("Personne", "Information cachée à tous"),
    FRIENDS("Amis", "Visible uniquement par mes amis"),
    CITY_MEMBERS("Membres de ma ville", "Visible par les membres de ma ville"),
    EVERYONE("Tout le monde", "Visible par tous les joueurs");

    private final String displayName;
    private final String description;

    VisibilityLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}