package fr.openmc.core.utils.freeze;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FreezeCommand {
	
	/**
	 * Freeze a player
	 *
	 * @param player The player who executes the command
	 * @param target The target player to freeze
	 */
	@Command("freeze")
	@CommandPermission("omc.admins.commands.freeze")
	public void onCommand(Player player, @Named("player") Player target) {
		FreezeManager.switchFreeze(player, target);
	}
}
