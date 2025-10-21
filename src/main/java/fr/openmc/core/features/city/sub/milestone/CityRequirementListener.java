package fr.openmc.core.features.city.sub.milestone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class CityRequirementListener implements Listener {
    private final List<EventCityRequirement> requirements = new ArrayList<>();

    public CityRequirementListener() {
        for (CityLevels level : CityLevels.values()) {
            for (CityRequirement requirement : level.getRequirements()) {
                if (requirement instanceof EventCityRequirement e) {
                    requirements.add(e);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(CraftItemEvent e) {
        for (EventCityRequirement req : requirements) {
            req.onEvent(e);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        for (EventCityRequirement req : requirements) {
            req.onEvent(e);
        }
    }
}
