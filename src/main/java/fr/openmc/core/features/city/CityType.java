package fr.openmc.core.features.city;

import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum CityType {
    WAR(TranslationManager.translation("feature.city.war.war")),
    PEACE(TranslationManager.translation("feature.city.war.peace")),
    ;

    private final Component displayName;

    CityType(Component displayName) {
        this.displayName = displayName;
    }
}
