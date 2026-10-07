package fr.openmc.core.features.tpa.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPACancelCommand {
	private final TPAManager tpaManager = OMCRegistry.FEATURES.TPA.get();

	/**
	 * Command to cancel a teleport request.
	 * @param player The player who wants to cancel the request.
	 */
	@Command("tpacancel")
	@CommandPermission("omc.commands.tpa")
	public void tpaCancel(OMCPlayer player) {
		if (!tpaManager.requesterHasPendingRequest(player)) {
			player.message().send(TranslationManager.translation("feature.tpa.cancel.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		OMCPlayer target = tpaManager.getTargetByRequester(player);

		if (target == null) {
			player.message().send(TranslationManager.translation("feature.tpa.cancel.player_not_online"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		tpaManager.removeRequest(player, target);
		player.message().send(TranslationManager.translation(
				"feature.tpa.cancel.success",
				target.getNameWithHead().color(NamedTextColor.GOLD)
		), Prefix.OPENMC, MessageType.SUCCESS, true);
		target.message().send(TranslationManager.translation(
				"feature.tpa.cancel.cancelled",
				player.getNameWithHead().color(NamedTextColor.DARK_RED)
		), Prefix.OPENMC, MessageType.INFO, true);

	}
}
