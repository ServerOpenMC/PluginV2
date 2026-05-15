package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.menu.NotationDialog;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class NotationCommands {
    @Command({"city notation"})
    @CommandPermission("omc.commands.city.notation")
    @Description("Ouvre le menu des notations")
    void notationTest(Player sender) {
        String weekStr = DateUtils.getWeekFormat();
        if (NotationManager.getSortedNotationForWeek(weekStr) == null) {
	        MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.notation.command.none"), Prefix.CITY, MessageType.INFO, false);
            return;
        }

        NotationDialog.send(sender, weekStr);
    }

}
