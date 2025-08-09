package fr.openmc.core.features.city.sub.notation.commands;

import fr.openmc.core.features.city.sub.notation.menu.NotationDialog;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class NotationCommands {
    @Command({"city notation"})
    @CommandPermission("omc.commands.city.notation")
    @Description("Ouvre le menu des notations")
    void notationTest(Player sender) {
        NotationDialog.send(sender, DateUtils.getWeekFormat());
    }

}
