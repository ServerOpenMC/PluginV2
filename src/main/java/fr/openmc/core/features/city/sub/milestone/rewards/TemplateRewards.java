package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import net.kyori.adventure.text.Component;

public class TemplateRewards implements CityRewards {

    private final Component message;

    public TemplateRewards(Component message) {
        this.message = message;
    }

    @Override
    public Component getName() {
        return message;
    }
}
