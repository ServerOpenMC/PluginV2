package fr.openmc.core.features.milestones;

import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("milestone")
@CommandPermission("omc.commands.milestone")
public class MilestoneCommand {
    @DefaultFor("~")
    void mainCommand(Player player) {
        new MainMilestonesMenu(player).open();
    }
}
