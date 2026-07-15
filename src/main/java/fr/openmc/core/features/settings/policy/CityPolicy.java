package fr.openmc.core.features.settings.policy;

import lombok.Getter;

@Getter
public enum CityPolicy implements Policy {
    EVERYONE("feature.settings.policy.city.everyone.name", "feature.settings.policy.city.everyone.description"),
    FRIENDS("feature.settings.policy.city.friends.name", "feature.settings.policy.city.friends.description"),
    NOBODY("feature.settings.policy.city.nobody.name", "feature.settings.policy.city.nobody.description");

    private final String displayName;
    private final String description;

    CityPolicy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
