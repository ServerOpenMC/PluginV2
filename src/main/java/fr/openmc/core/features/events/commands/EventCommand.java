package fr.openmc.core.features.events.commands;

import fr.openmc.core.features.events.menu.MainEventMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;

@Command({"events"})
@Description("Ouvre le menu des événements")
public class EventCommand {
    @Cooldown(2)
    @CommandPlaceholder()
    public static void mainCommand(Player player) {
        new MainEventMenu(player).open();
    }
}
