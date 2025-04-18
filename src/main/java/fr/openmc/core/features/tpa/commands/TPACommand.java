package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
	@CommandPermission("ayw.command.tpa")
	@AutoComplete("@players")
	public void tpAsk(Player player, @Named("player") Player target) {
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Le joueur n'existe pas ou n'est pas en ligne"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (player == target) {
			MessagesManager.sendMessage(player, Component.text("§4Vous ne pouvez pas vous envoyer de demande de téléportation à vous même"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAQueue.QUEUE.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous avez déjà une demande de téléportation en attente"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		sendTPARequest(player, target);
	}
	
	private void sendTPARequest(Player player, Player target) {
		TPAQueue.QUEUE.addRequest(player, target);
		
		MessagesManager.sendMessage(target, Component.text("§3Le joueur §6" + player.getName() + " §3 veut se téléporter à vous\n" +
				"§3Tapez §5/tpaccept §3 pour accepter et §5/tpdeny §3 pour refuser"), Prefix.OPENMC, MessageType.INFO, true);
		MessagesManager.sendMessage(player, Component.text("§2Vous avez envoyé une demande de téléportation à §6" + target.getName() + " \n" +
				"§3Tapez §5/tpcancel §3 pour annuler votre demande de tp"), Prefix.OPENMC, MessageType.SUCCESS, true);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				TPAQueue.QUEUE.expireRequest(player, target);
			}
		}.runTaskLater(plugin, 600);
	}
	
}
