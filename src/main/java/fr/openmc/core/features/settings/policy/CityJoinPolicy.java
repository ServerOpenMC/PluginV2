package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum CityJoinPolicy {
    EVERYONE("Tout le monde", "Accepter les demandes de tous les joueurs"),
    FRIENDS_ONLY("Amis uniquement", "Accepter uniquement les demandes de mes amis"),
    NOBODY("Personne", "Refuser toutes les demandes");

    private final String displayName;
    private final String description;

    CityJoinPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}