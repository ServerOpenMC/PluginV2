package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.tpa.commands.autocomplete.TpaPendingAutoComplete;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
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
			Player target,
			@Optional @SuggestWith(TpaPendingAutoComplete.class) @Named("player")
			Player player
	) {
		if (!TPAManager.hasPendingRequest(target)) {
			MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.deny.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAManager.hasMultipleRequests(target)) {
			if (player == null) {
				MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.deny.multiple_requests"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!TPAManager.getRequesters(target).contains(player)) {
				MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.deny.no_request_from", Component.text(player.getName())), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = TPAManager.getRequesters(target).getFirst();
		}
		
		MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.deny.success", Component.text(player.getName())), Prefix.OPENMC, MessageType.SUCCESS, false);
		MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.deny.denied", Component.text(target.getName())), Prefix.OPENMC, MessageType.ERROR, false);

		TPAManager.removeRequest(player, target);
	}
	
}
