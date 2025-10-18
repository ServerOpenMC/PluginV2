package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.milestone.commands.AdminCityMilestoneCommands;
import fr.openmc.core.features.city.sub.milestone.commands.CityMilestoneCommands;
import fr.openmc.core.features.city.sub.milestone.listeners.CooldownEndListener;
import fr.openmc.core.features.city.sub.statistics.listeners.MemberJoinListener;

public class CityMilestoneManager {
    public static void init() {
        CommandsManager.getHandler().register(
                new AdminCityMilestoneCommands(),
                new CityMilestoneCommands()
        );

        OMCPlugin.registerEvents(
                new CooldownEndListener(),
                new CityRequirementListener(),
                new MemberJoinListener()
        );
    }
}
