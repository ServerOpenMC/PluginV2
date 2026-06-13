package fr.openmc.core.commands.admin.freeze;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class FreezeManager extends Feature implements HasCommands, HasListeners {
	
	public static final Set<Player> FROZEN_PLAYERS = new HashSet<>();
	private static Player player;

	@Override
	public Set<Object> getCommands() {
		return Set.of(
				new FreezeCommand()
		);
	}

	@Override
	public Set<Listener> getListeners() {
		return Set.of(
				new FreezeListener()
		);
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
			MessagesManager.sendMessage(player, TranslationManager.translation("command.admin.freeze.player_not_found"), Prefix.OPENMC, MessageType.ERROR, false);
		} else {
			if (FROZEN_PLAYERS.contains(target)) {
				target.setInvulnerable(false);
				FROZEN_PLAYERS.remove(target);
				MessagesManager.sendMessage(player,
						TranslationManager.translation("command.admin.freeze.player_unfreeze",
								Component.text(target.getName()).color(NamedTextColor.GOLD)), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, TranslationManager.translation("command.admin.freeze.target_unfreeze"), Prefix.OPENMC, MessageType.INFO, true);
			} else {
				target.setInvulnerable(true);
				Location location = target.getLocation();
				location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
				target.teleport(location);
				FROZEN_PLAYERS.add(target);
                target.sendTitlePart(TitlePart.TITLE, TranslationManager.translation("command.admin.freeze.title.1"));
                target.sendTitlePart(TitlePart.SUBTITLE, TranslationManager.translation("command.admin.freeze.title.2"));
                MessagesManager.sendMessage(player,
						TranslationManager.translation("command.admin.freeze.player_freeze",
								Component.text(target.getName()).color(NamedTextColor.GOLD)), Prefix.OPENMC, MessageType.SUCCESS, false);
				MessagesManager.sendMessage(target, TranslationManager.translation("command.admin.freeze.target_freeze"), Prefix.OPENMC, MessageType.WARNING, true);
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
			case KICKED -> MessagesManager.sendMessage(player, TranslationManager.translation("command.admin.freeze.quit_reason.kicked"), Prefix.OPENMC, MessageType.INFO, true);
			case TIMED_OUT -> MessagesManager.sendMessage(player, TranslationManager.translation("command.admin.freeze.quit_reason.timeout"), Prefix.OPENMC, MessageType.INFO, true);
			case ERRONEOUS_STATE -> MessagesManager.sendMessage(player, TranslationManager.translation("command.admin.freeze.quit_reason.error"), Prefix.OPENMC, MessageType.INFO, true);
			default -> MessagesManager.sendMessage(player, TranslationManager.translation("command.admin.freeze.quit_reason.default"), Prefix.OPENMC, MessageType.INFO, true);
		}
	}
}
