package fr.openmc.core.features.tpa.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.tpa.commands.autocomplete.TpaPendingAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.NamedTextColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPADenyCommand {

	/**
	 * Command to deny a teleportation request
	 * @param target The player denying the request.
	 * @param player The player who sent the teleportation request (optional).
	 */
	@Command("tpadeny")
	@CommandPermission("omc.commands.tpa")
	public void tpaDeny(
			OMCPlayer target,
			@Optional @SuggestWith(TpaPendingAutoComplete.class) @Named("player")
			OMCPlayer player
	) {
		if (!target.tpa().hasPendingRequest()) {
			target.message().send(TranslationManager.translation("feature.tpa.deny.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (target.tpa().hasMultipleRequests()) {
			if (player == null) {
				target.message().send(TranslationManager.translation("feature.tpa.deny.multiple_requests"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!target.tpa().getRequesters().contains(player)) {
				player.message().send(TranslationManager.translation(
						"feature.tpa.deny.no_request_from",
						player.getNameWithHead().color(NamedTextColor.GOLD)
				), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = target.tpa().getRequesters().getFirst();
		}
		
		target.message().send(TranslationManager.translation(
				"feature.tpa.deny.success",
				player.getNameWithHead().color(NamedTextColor.GOLD)
		), Prefix.OPENMC, MessageType.SUCCESS, false);
		player.message().send(TranslationManager.translation(
				"feature.tpa.deny.denied",
				target.getNameWithHead().color(NamedTextColor.GOLD)
		), Prefix.OPENMC, MessageType.ERROR, false);

		player.tpa().removeRequest(target);
	}
	
}
