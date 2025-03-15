package fr.openmc.core.features.tpa;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPAcceptCommand {
	
	@Command("tpaccept")
	@CommandPermission("ayw.command.tpa")
	public void tpAccept(Player player) {
		if (!TPAQueue.QUEUE.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		Player requester = TPAQueue.QUEUE.getRequester(player);
		
		if (player.getFallDistance() > 0) {
			MessagesManager.sendMessage(player, Component.text("§4Le joueur est en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			MessagesManager.sendMessage(requester, Component.text("§4Vous êtes en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		Location loc = player.getLocation();
		requester.teleport(loc);
		MessagesManager.sendMessage(player, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);
		MessagesManager.sendMessage(requester, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);
		
		TPAQueue.QUEUE.removeRequest(player);
	}
}
