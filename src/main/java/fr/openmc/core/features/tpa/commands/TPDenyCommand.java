package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPDenyCommand {
	
	/**
	 * Command to deny a teleportation request
	 * @param target The player denying the request.
	 * @param player The player who sent the teleportation request (optional).
	 */
	@Command("tpdeny")
	@CommandPermission("omc.commands.tpa")
	public void tpDeny(Player target, @Optional @Named("player") Player player) {
		if (!TPAQueue.QUEUE.hasPendingRequest(target)) {
			MessagesManager.sendMessage(target, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAQueue.QUEUE.hasMultipleRequests(target)) {
			if (player == null) {
				MessagesManager.sendMessage(target, Component.text("§4Vous avez plusieurs demandes de téléportation en cours, utilisez §6/tpdeny <joueur>"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!TPAQueue.QUEUE.getRequesters(target).contains(player)) {
				MessagesManager.sendMessage(target, Component.text("§4Vous n'avez pas de demande de téléportation de la part de §6" + player.getName()), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = TPAQueue.QUEUE.getRequesters(target).getFirst();
		}
		
		MessagesManager.sendMessage(target, Component.text("§2Vous avez refusé la demande de téléportation de §6" + player.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
		MessagesManager.sendMessage(player, Component.text("§6" + target.getName() + " §4a refusé votre demande de téléportation"), Prefix.OPENMC, MessageType.ERROR, false);
		
		TPAQueue.QUEUE.removeRequest(player, target);
	}
	
}
