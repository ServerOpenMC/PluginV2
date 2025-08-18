package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.EventCityRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CommandRequirement implements EventCityRequirement {

    private final String command;
    private final int amountRequired;

    public CommandRequirement(String command, int amountRequired) {
        this.command = command;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isDone(City city) {
        return Objects.requireNonNull(CityStatisticsManager.getStat(city.getUUID(), getScope())).asInt() >= amountRequired;
    }

    @Override
    public String getScope() {
        return "command_" + command;
    }

    @Override
    public ItemStack getIcon(City city) {
        return ItemStack.of(Material.COMMAND_BLOCK, amountRequired);
    }

    @Override
    public Component getName(City city) {
        return Component.text("Exécuter " + amountRequired + " fois " + command);
    }

    @Override
    public Component getDescription() {
        return null;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof PlayerCommandPreprocessEvent e)) return;

        String cmd = e.getMessage().split(" ")[0].substring(1).toLowerCase();
        if (!cmd.equals(command)) return;

        Player player = e.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        if (Objects.requireNonNull(CityStatisticsManager.getStat(playerCity.getUUID(), getScope())).asInt() >= amountRequired)
            return;

        CityStatisticsManager.increment(playerCity.getUUID(), getScope(), 1);
    }
}
