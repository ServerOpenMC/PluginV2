package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum FriendPolicy implements Policy {
    EVERYONE("feature.settings.policy.friend.everyone.name", "feature.settings.policy.friend.everyone.description"),
    CITY_MEMBERS_ONLY("feature.settings.policy.friend.city_members_only.name", "feature.settings.policy.friend.city_members_only.description"),
    NOBODY("feature.settings.policy.friend.nobody.name", "feature.settings.policy.friend.nobody.description");

    private final String displayName;
    private final String description;

    FriendPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
