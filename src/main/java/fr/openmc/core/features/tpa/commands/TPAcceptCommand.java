package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.tpa.TPAQueue;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPAcceptCommand {
	
	@Command("tpaccept")
	@CommandPermission("ayw.command.tpa")
	public void tpAccept(Player player) {
		if (! TPAQueue.QUEUE.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous n'avez aucune demande de téléportation en cours"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		Player requester = TPAQueue.QUEUE.getRequester(player);
		
		if (player.getFallDistance() > 0) {
			MessagesManager.sendMessage(player, Component.text("§4Le joueur est en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			MessagesManager.sendMessage(requester, Component.text("§4Vous êtes en train de tomber, téléportation impossible"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Location loc = player.getLocation();
				requester.sendTitle(PlaceholderAPI.setPlaceholders(requester, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
				new BukkitRunnable() {
					@Override
					public void run() {
						requester.teleport(loc);
						MessagesManager.sendMessage(player, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);
						MessagesManager.sendMessage(requester, Component.text("§2Téléportation réussie"), Prefix.OPENMC, MessageType.SUCCESS, true);

						TPAQueue.QUEUE.removeRequest(player);
					}
					}.runTaskLater(OMCPlugin.getInstance(), 10);
				}
		}.runTaskLater(OMCPlugin.getInstance(), 10);
	}
}
