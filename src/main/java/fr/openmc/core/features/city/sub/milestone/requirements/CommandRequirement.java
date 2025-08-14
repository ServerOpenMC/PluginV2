package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.EventCityRequirement;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class CommandRequirement implements EventCityRequirement {

    private final String command;
    private final int amountRequired;

    public CommandRequirement(String command, int amountRequired) {
        this.command = command;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isDone(City city) {
        return false; //todo: CityStatistics
    }

    @Override
    public ItemStack getIcon() {
        return ItemStack.of(Material.COMMAND_BLOCK, amountRequired);
    }

    @Override
    public Component getName() {
        return Component.text("Exécuter " + amountRequired + " " + command);
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

        // todo: CityStatistics +1 in the amount of commands "command" executed
    }
}
