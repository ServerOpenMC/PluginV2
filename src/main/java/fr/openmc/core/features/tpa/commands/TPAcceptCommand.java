package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPAcceptCommand {
	
	/**
	 * Accept a teleportation request from another player.
	 *
	 * @param target The player who accepts the teleportation request.
	 * @param player The player who sent the teleportation request (optional).
	 */
	@Command("tpaccept")
	@CommandPermission("omc.commands.tpa")
	public void tpAccept(Player target, @Optional @Named("player") Player player) {
		if (!TPAQueue.QUEUE.hasPendingRequest(target)) {
			MessagesManager.sendMessage(target, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAQueue.QUEUE.hasMultipleRequests(target)) {
			if (player == null) {
				MessagesManager.sendMessage(target, Component.text("§4Vous avez plusieurs demandes de téléportation en cours, utilisez §6/tpaccept <joueur>"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!TPAQueue.QUEUE.getRequesters(target).contains(player)) {
				MessagesManager.sendMessage(target, Component.text("§4Vous n'avez pas de demande de téléportation de la part de §6" + player.getName()), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = TPAQueue.QUEUE.getRequesters(target).getFirst();
		}
		
		if (target.getFallDistance() > 0) {
			MessagesManager.sendMessage(target, Component.text("§4Le joueur est en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			MessagesManager.sendMessage(player, Component.text("§4Vous êtes en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		if (!player.isOnline()) {
			MessagesManager.sendMessage(target, Component.text("§4Le joueur n'est pas en ligne ou n'existe pas"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		if (TPAQueue.QUEUE.getTargetByRequester(player) != null) {
			if (TPAQueue.QUEUE.getTargetByRequester(player).equals(target)) teleport(player, target);
		}
	}
	
	private void teleport(Player requester, Player target) {
		Location loc = target.getLocation();
		PlayerUtils.sendFadeTitleTeleport(requester, loc);
		MessagesManager.sendMessage(target, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);
		MessagesManager.sendMessage(requester, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);
		TPAQueue.QUEUE.removeRequest(requester, target);
	}
}
