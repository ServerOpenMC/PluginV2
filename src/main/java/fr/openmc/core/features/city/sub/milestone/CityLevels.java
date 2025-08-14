package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import net.kyori.adventure.text.Component;

import java.util.List;

public enum CityLevels {
    LEVEL_1(
            Component.text("Niveau 1"),
            Component.text("Ere Urbaine"),
            List.of(
                    new CommandRequirement("/city create", 1)
            )
    ),
    LEVEL_2(
            Component.text("Niveau 2"),
            Component.text("Les Fondations"),
            List.of(
                    new CommandRequirement("/city create", 1)
            )
    ),
    ;

    private final Component name;
    private final Component description;
    private final List<CityRequirement> requirements;

    CityLevels(Component name, Component description, List<CityRequirement> requirements) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
    }
}
