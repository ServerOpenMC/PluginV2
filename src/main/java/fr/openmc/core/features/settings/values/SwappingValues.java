package fr.openmc.core.features.settings.values;

import fr.openmc.core.features.settings.policy.Policy;
import lombok.Getter;

@Getter
public enum SwappingValues implements Policy {
    FULL("feature.settings.values.swapping.full.name", "feature.settings.values.swapping.full.description", -1),
    SLOW("feature.settings.values.swapping.slow.name", "feature.settings.values.swapping.slow.description", 120),
    NORMAL("feature.settings.values.swapping.normal.name", "feature.settings.values.swapping.normal.description", 60),
    FAST("feature.settings.values.swapping.fast.name", "feature.settings.values.swapping.fast.description", 30);

    private final String displayName;
    private final String description;
    private final int swappingTime; // En seconds

    SwappingValues(String displayName, String description, int swappingTime) {
        this.displayName = displayName;
        this.description = description;
        this.swappingTime = swappingTime;
    }
}