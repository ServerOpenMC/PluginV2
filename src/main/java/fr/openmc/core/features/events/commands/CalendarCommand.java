package fr.openmc.core.features.events.commands;

import fr.openmc.core.features.events.menu.CalendarMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;

@Command({"events calendar"})
@Description("Ouvre le calendrier")
public class CalendarCommand {
    @Cooldown(2)
    @CommandPlaceholder()
    public static void mainCommand(Player player) {
        new CalendarMenu(player).open();
    }
}
