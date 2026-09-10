package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.commands.AdminCityMilestoneCommands;
import fr.openmc.core.features.city.sub.milestone.commands.CityMilestoneCommands;
import fr.openmc.core.features.city.sub.milestone.listeners.CooldownEndListener;
import fr.openmc.core.features.city.sub.statistics.listeners.MemberJoinListener;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;

import java.util.Set;

public class CityMilestoneManager extends Feature implements HasCommands, HasListeners {
    private final CityManager cityManager;

    public CityMilestoneManager(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminCityMilestoneCommands(),
                new CityMilestoneCommands()
        );
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                () -> new CooldownEndListener(cityManager),
                CityRequirementListener::new,
                MemberJoinListener::new
        );
    }
}
