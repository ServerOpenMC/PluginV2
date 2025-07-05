package fr.openmc.core.features.milestones;

import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("milestones")
@CommandPermission("omc.commands.milestones")
public class MilestoneCommand {
    @DefaultFor("~")
    void mainCommand(Player player) {
        new MainMilestonesMenu(player).open();
    }
}
