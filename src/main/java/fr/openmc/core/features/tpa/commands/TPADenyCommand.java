package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCRegistry;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.tpa.commands.autocomplete.TpaPendingAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPADenyCommand {
	private final TPAManager tpaManager = OMCRegistry.FEATURES.TPA.get();

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
		if (!tpaManager.hasPendingRequest(target)) {
			target.message().send(TranslationManager.translation("feature.tpa.deny.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (tpaManager.hasMultipleRequests(target)) {
			if (player == null) {
				target.message().send(TranslationManager.translation("feature.tpa.deny.multiple_requests"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!tpaManager.getRequesters(target).contains(player)) {
				player.message().send(TranslationManager.translation(
						"feature.tpa.deny.no_request_from",
						player.getNameWithHead().color(NamedTextColor.GOLD)
				), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = tpaManager.getRequesters(target).getFirst();
		}
		
		target.message().send(TranslationManager.translation(
				"feature.tpa.deny.success",
				player.getNameWithHead().color(NamedTextColor.GOLD)
		), Prefix.OPENMC, MessageType.SUCCESS, false);
		player.message().send(TranslationManager.translation(
				"feature.tpa.deny.denied",
				target.getNameWithHead().color(NamedTextColor.GOLD)
		), Prefix.OPENMC, MessageType.ERROR, false);

		tpaManager.removeRequest(player, target);
	}
	
}
