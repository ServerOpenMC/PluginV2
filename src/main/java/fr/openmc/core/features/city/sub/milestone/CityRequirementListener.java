package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.EventTemplateRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
