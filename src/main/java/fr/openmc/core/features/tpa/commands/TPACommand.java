package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPACommand {
	
	private final OMCPlugin plugin;
	
	public TPACommand(OMCPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Command to send a teleport request to a player.
	 * @param player The player sending the request.
	 * @param target The target player to whom the request is sent.
	 */
	@Command({"tpa", "tpask"})
	@CommandPermission("omc.commands.tpa")
	@AutoComplete("@players")
	public void tpAsk(Player player, @Named("player") Player target) {
		if (TPAQueue.QUEUE.requesterHasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous avez déjà une demande de téléportation en attente\n")
					.append(Component.text("§3Tapez §5/tpcancel §3pour annuler votre demande de tp en cours").clickEvent(ClickEvent.runCommand("/tpcancel")).hoverEvent(HoverEvent.showText(Component.text("Annuler la demande de TP")))
					), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Le joueur n'existe pas ou n'est pas en ligne"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (player == target) {
			MessagesManager.sendMessage(player, Component.text("§4Vous ne pouvez pas vous envoyer de demande de téléportation à vous même"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAQueue.QUEUE.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous avez déjà une demande de téléportation en attente de votre acceptation"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		sendTPARequest(player, target);
	}
	
	private void sendTPARequest(Player player, Player target) {
		TPAQueue.QUEUE.addRequest(player, target);
		
		MessagesManager.sendMessage(target,
				Component.text("§3Le joueur §6" + player.getName() + " §3 veut se téléporter à vous\n")
						.append(Component.text("§3Tapez §5/tpaccept §3pour accepter").clickEvent(ClickEvent.runCommand("/tpaccept")).hoverEvent(HoverEvent.showText(Component.text("Accepter la demande de TP")))
						.append(Component.text("§3 et §5/tpdeny §3pour refuser").clickEvent(ClickEvent.runCommand("/tpdeny")).hoverEvent(HoverEvent.showText(Component.text("Refuser la demande de TP")))
						)),
				Prefix.OPENMC, MessageType.INFO, true);
		MessagesManager.sendMessage(player, Component.text("§2Vous avez envoyé une demande de téléportation à §6" + target.getName() + " \n")
				.append(Component.text("§3Tapez §5/tpcancel §3pour annuler votre demande de tp").clickEvent(ClickEvent.runCommand("/tpcancel")).hoverEvent(HoverEvent.showText(Component.text("Annuler la demande de TP")))
				), Prefix.OPENMC, MessageType.SUCCESS, true);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				TPAQueue.QUEUE.expireRequest(player, target);
			}
		}.runTaskLater(plugin, 600);
	}

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

	/**
	 * Command to cancel a teleport request.
	 * @param player The player who wants to cancel the request.
	 */
	@Command("tpcancel")
	@CommandPermission("omc.commands.tpa")
	public void tpCancel(Player player) {
		if (!TPAQueue.QUEUE.requesterHasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}

		Player target = TPAQueue.QUEUE.getTargetByRequester(player);

		TPAQueue.QUEUE.removeRequest(player, target);
		MessagesManager.sendMessage(player, Component.text("§2Vous avez annulé votre demande de téléportation à §6" + target.getName()), Prefix.OPENMC, MessageType.SUCCESS, true);
		MessagesManager.sendMessage(target, Component.text("§3" + player.getName() + " §4a annulé sa demande de téléportation"), Prefix.OPENMC, MessageType.INFO, true);

	}

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
			if (TPAQueue.QUEUE.getTargetByRequester(player).equals(target))
				teleport(player, target);
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
