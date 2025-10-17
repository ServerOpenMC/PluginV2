package fr.openmc.core.commands.admin.freeze;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
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
	public void onCommand(Player player, @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player target) {
		FreezeManager.switchFreeze(player, target);
	}
}
