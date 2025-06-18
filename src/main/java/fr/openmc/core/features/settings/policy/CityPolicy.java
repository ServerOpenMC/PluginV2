package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum CityPolicy {
    EVERYONE("§aTout le monde", "Accepter les demandes de tous les joueurs"),
    FRIENDS("§dAmis uniquement", "Accepter uniquement les demandes de mes amis"),
    NOBODY("§cPersonne", "Refuser toutes les demandes");

    private final String displayName;
    private final String description;

    CityPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}