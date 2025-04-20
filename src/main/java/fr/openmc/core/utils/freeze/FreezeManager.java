package fr.openmc.core.utils.freeze;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class FreezeManager {
	
	public static final List<Player> FROZEN_PLAYERS = new ArrayList<>();
	private static Player player;
	
	public FreezeManager() {
		Bukkit.getServer().getPluginManager().registerEvents(new FreezeListener(), OMCPlugin.getInstance());
	}
	
	/**
	 * Freeze or unfreeze a player
	 *
	 * @param player The player who freeze/unfreeze
	 * @param target The player to freeze/unfreeze
	 */
	public static void switchFreeze(Player player, Player target) {
		FreezeManager.player = player;
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Joueur introuvable"), Prefix.OPENMC, MessageType.ERROR, false);
		} else {
			if (FROZEN_PLAYERS.contains(target)) {
				target.setInvulnerable(false);
				FROZEN_PLAYERS.remove(target);
				MessagesManager.sendMessage(player, Component.text("§2Vous avez unfreeze §6 " + target.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, Component.text("§4Vous avez été unfreeze"), Prefix.OPENMC, MessageType.INFO, true);
			} else {
				target.setInvulnerable(true);
				Location location = target.getLocation();
				location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
				target.teleport(location);
				FROZEN_PLAYERS.add(target);
				target.sendTitle("§4Vous êtes freeze", "§5Si vous vous déconnectez, vous serez banni");
				MessagesManager.sendMessage(player, Component.text("§2Vous avez freeze §6" + target.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, Component.text("§4Vous avez été freeze"), Prefix.OPENMC, MessageType.WARNING, true);
			}
		}
	}
	
	/**
	 * Contact the freezer to explain the reason of the disconnection
	 *
	 * @param reason The reason of the disconnection
	 */
	public static void contactFreezer(PlayerQuitEvent.QuitReason reason) {
		if (player == null) return;
		switch (reason) {
			case KICKED -> MessagesManager.sendMessage(player, Component.text("§4Le joueur a été kick"), Prefix.OPENMC, MessageType.INFO, true);
			case TIMED_OUT -> MessagesManager.sendMessage(player, Component.text("§4Le joueur a été time out"), Prefix.OPENMC, MessageType.INFO, true);
			case ERRONEOUS_STATE -> MessagesManager.sendMessage(player, Component.text("§4Le joueur a eu une erreur de connexion"), Prefix.OPENMC, MessageType.INFO, true);
			default -> MessagesManager.sendMessage(player, Component.text("§4Le joueur a été déconnecté"), Prefix.OPENMC, MessageType.INFO, true);
		}
	}
}
