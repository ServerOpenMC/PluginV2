package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum FriendPolicy {
    EVERYONE("§aTout le monde", "Accepter les demandes d'amis de tous les joueurs"),
    CITY_MEMBERS_ONLY("§6Membres de ma ville", "Accepter uniquement les demandes des membres de ma ville"),
    NOBODY("§cPersonne", "Refuser toutes les demandes d'amis");

    private final String displayName;
    private final String description;

    FriendPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}