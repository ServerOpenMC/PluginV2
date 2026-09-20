package fr.openmc.core.features.reports.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Optional;

@Command({"report", "rpt"})
public class ReportCommand {
    @CommandPlaceholder
    void mainCommand(CommandSender sender, @Optional Player target, @Optional String description){
        //TODO : Command report, si pas d'argument ouvrir le menu
    }
}
