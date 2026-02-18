package fr.openmc.core.features.milestones;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("milestones")
@CommandPermission("omc.commands.milestones")
public class MilestoneCommand {
    @CommandPlaceholder()
    void mainCommand(Player player) {
		if (DreamUtils.isInDreamWorld(player)) new MilestoneMenu(player, MilestoneType.DREAM.getMilestone()).open();
		else new MainMilestonesMenu(player).open();
    }
}
