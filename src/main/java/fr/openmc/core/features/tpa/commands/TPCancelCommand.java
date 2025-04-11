package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPCancelCommand {
	
	/**
	 * Command to cancel a teleport request.
	 * @param player The player who wants to cancel the request.
	 */
	@Command("tpacancel")
	@CommandPermission("ayw.command.tpa")
	public void tpCancel(Player player) {
		Player target = TPAQueue.QUEUE.getRequester(player);
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Vous n'avez pas de demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		TPAQueue.QUEUE.removeRequest(target);
		MessagesManager.sendMessage(player, Component.text("§2Vous avez annulé votre demande de téléportation à §6" + target.getName()), Prefix.OPENMC, MessageType.SUCCESS, true);
		MessagesManager.sendMessage(target, Component.text("§3" + player.getName() + " §4a annulé sa demande de téléportation"), Prefix.OPENMC, MessageType.INFO, true);
		
	}
}
