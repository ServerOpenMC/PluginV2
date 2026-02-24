package fr.openmc.core.commands.admin.freeze;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class FreezeManager {
	
	public static final Set<Player> FROZEN_PLAYERS = new HashSet<>();
	private static Player player;

	public static void init() {
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
			MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.player_not_found"), Prefix.OPENMC, MessageType.ERROR, false);
		} else {
			if (FROZEN_PLAYERS.contains(target)) {
				target.setInvulnerable(false);
				FROZEN_PLAYERS.remove(target);
				MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.player_unfreeze", target.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, Component.translatable("command.admin.freeze.target_unfreeze"), Prefix.OPENMC, MessageType.INFO, true);
			} else {
				target.setInvulnerable(true);
				Location location = target.getLocation();
				location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
				target.teleport(location);
				FROZEN_PLAYERS.add(target);
                target.sendTitlePart(TitlePart.TITLE, Component.translatable("command.admin.freeze.title.1"));
                target.sendTitlePart(TitlePart.SUBTITLE, Component.translatable("command.admin.freeze.title.2"));
                MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.player_freeze", target.getName()), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, Component.translatable("command.admin.freeze.target_freeze"), Prefix.OPENMC, MessageType.WARNING, true);
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
			case KICKED -> MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.quit_reason.kicked"), Prefix.OPENMC, MessageType.INFO, true);
			case TIMED_OUT -> MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.quit_reason.timeout"), Prefix.OPENMC, MessageType.INFO, true);
			case ERRONEOUS_STATE -> MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.quit_reason.error"), Prefix.OPENMC, MessageType.INFO, true);
			default -> MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.quit_reason.default"), Prefix.OPENMC, MessageType.INFO, true);
		}
	}
}
