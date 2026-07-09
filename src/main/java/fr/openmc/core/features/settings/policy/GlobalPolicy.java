package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum GlobalPolicy implements Policy {
    EVERYONE("feature.settings.policy.global.everyone.name", "feature.settings.policy.global.everyone.description"),
    FRIENDS("feature.settings.policy.global.friends.name", "feature.settings.policy.global.friends.description"),
    CITY_MEMBERS("feature.settings.policy.global.city_members.name", "feature.settings.policy.global.city_members.description"),
    NOBODY("feature.settings.policy.global.nobody.name", "feature.settings.policy.global.nobody.description");

    private final String displayName;
    private final String description;

    GlobalPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
