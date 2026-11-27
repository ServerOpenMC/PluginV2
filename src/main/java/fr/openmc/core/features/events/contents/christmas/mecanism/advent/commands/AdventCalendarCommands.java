package fr.openmc.core.features.events.contents.christmas.mecanism.advent.commands;

import fr.openmc.core.features.events.contents.christmas.mecanism.advent.AdventCalendar;
import fr.openmc.core.features.events.contents.christmas.mecanism.advent.menu.AdventCalendarMenu;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdventCalendarCommands {
    @Command("christmas calendar")
    @CommandPermission("omc.commands.christmas.calendar")
    void openCalendar(Player player) {
        if (AdventCalendar.isCalendarActive())
            new AdventCalendarMenu(player).open();
        else {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.events.christmas.calendar.ended"),
                    Prefix.CHRISTMAS, MessageType.ERROR, false);
        }
    }
}
