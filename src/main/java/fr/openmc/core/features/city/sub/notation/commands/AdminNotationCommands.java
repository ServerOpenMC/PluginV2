package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.core.features.city.sub.notation.menu.NotationEditionDialog;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;


public class AdminNotationCommands {
    @Command({"admcity notation"})
    @CommandPermission("omc.admins.commands.admcity.notation")
    public void notation(Player sender) {
        NotationEditionDialog.send(sender, DateUtils.getNextWeekFormat(), null); //todo: sort all edited notation from all city
    }
}