package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPACancelCommand {
	
	/**
	 * Command to cancel a teleport request.
	 * @param player The player who wants to cancel the request.
	 */
	@Command("tpacancel")
	@CommandPermission("omc.commands.tpa")
	public void tpaCancel(Player player) {
		if (!TPAManager.requesterHasPendingRequest(player)) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.cancel.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		Player target = TPAManager.getTargetByRequester(player);

		if (target == null) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.cancel.player_not_online"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		TPAManager.removeRequest(player, target);
		MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.cancel.success", Component.text(target.getName())), Prefix.OPENMC, MessageType.SUCCESS, true);
		MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.cancel.cancelled", Component.text(player.getName())), Prefix.OPENMC, MessageType.INFO, true);

	}
}
