package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPDenyCommand {
	
	/**
	 * Command to deny a teleportation request
	 * @param player The player denying the request.
	 */
	@Command("tpdeny")
	@CommandPermission("ayw.command.tpa")
	public void tpDeny(Player player) {
		if (! TPAQueue.QUEUE.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		Player requester = TPAQueue.QUEUE.getRequester(player);
		
		MessagesManager.sendMessage(player, Component.text("§2Vous avez refusé la demande de téléportation de §6" + requester.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
		MessagesManager.sendMessage(requester, Component.text("§6" + player.getName() + " §4a refusé votre demande de téléportation"), Prefix.OPENMC, MessageType.ERROR, false);
		
		TPAQueue.QUEUE.removeRequest(player);
	}
	
}
