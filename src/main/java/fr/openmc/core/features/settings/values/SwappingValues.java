package fr.openmc.core.features.settings.values;

import fr.openmc.core.features.settings.policy.Policy;
import lombok.Getter;

@Getter
public enum SwappingValues implements Policy {
    FULL("feature.settings.policy.city.everyone.name", "feature.settings.policy.city.everyone.description", -1),//TODO
    SLOW("feature.settings.policy.city.friends.name", "feature.settings.policy.city.friends.description", 120),//TODO
    NORMAL("feature.settings.policy.city.nobody.name", "feature.settings.policy.city.nobody.description", 60),//TODO
    FAST("feature.settings.policy.city.nobody.name", "feature.settings.policy.city.nobody.description", 30);//TODO

    private final String displayName;
    private final String description;
    private final int swappingTime; // En seconds

    SwappingValues(String displayName, String description, int swappingTime) {
        this.displayName = displayName;
        this.description = description;
        this.swappingTime = swappingTime;
    }
}