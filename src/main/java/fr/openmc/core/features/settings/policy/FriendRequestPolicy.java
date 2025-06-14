package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum FriendRequestPolicy {
    EVERYONE("Tout le monde", "Accepter les demandes d'amis de tous les joueurs"),
    CITY_MEMBERS_ONLY("Membres de ma ville", "Accepter uniquement les demandes des membres de ma ville"),
    NOBODY("Personne", "Refuser toutes les demandes d'amis");

    private final String displayName;
    private final String description;

    FriendRequestPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}