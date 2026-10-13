package fr.openmc.core.features.tpa.commands;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.features.tpa.commands.autocomplete.TpaPendingAutoComplete;
import fr.openmc.core.utils.bukkit.PlayerUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

public class TPAcceptCommand {
	
	/**
	 * Accept a teleportation request from another player.
	 *
	 * @param target The player who accepts the teleportation request.
	 * @param player The player who sent the teleportation request (optional).
	 */
	@Command("tpaccept")
	@CommandPermission("omc.commands.tpa")
	public void tpAccept(
			OMCPlayer target,
			@Optional @SuggestWith(TpaPendingAutoComplete.class) @Named("player") OMCPlayer player
	) {
		if (!TPAManager.hasPendingRequest(target)) {
			MessagesManager.sendMessage(target, TranslationManager.translation("feature.tpa.accept.no_pending"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAManager.hasMultipleRequests(target)) {
			if (player == null) {
				target.message().send(TranslationManager.translation("feature.tpa.accept.multiple_requests"), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
			
			if (!TPAManager.getRequesters(target).contains(player)) {
				target.message().send(TranslationManager.translation(
						"feature.tpa.accept.no_request_from",
						player.getNameWithHead().color(NamedTextColor.GOLD)
				), Prefix.OPENMC, MessageType.ERROR, false);
				return;
			}
		} else {
			player = TPAManager.getRequesters(target).getFirst();
		}
		
		if (target.getFallDistance() > 0) {
			target.message().send(TranslationManager.translation("feature.tpa.accept.falling"), Prefix.OPENMC, MessageType.ERROR, true);
			player.message().send(TranslationManager.translation("feature.tpa.accept.you_falling"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		if (!player.isOnline()) {
			target.message().send(TranslationManager.translation("feature.tpa.accept.player_not_online"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		if (TPAManager.getTargetByRequester(player) != null) {
			if (Objects.equals(TPAManager.getTargetByRequester(player), target)) teleport(player, target);
		}
	}
	
	private void teleport(OMCPlayer requester, OMCPlayer target) {
		Location loc = target.getLocation();
		PlayerUtils.sendFadeTitleTeleport(requester, loc);
		target.message().send(TranslationManager.translation("feature.tpa.accept.success"), Prefix.OPENMC, MessageType.SUCCESS, true);
		requester.message().send(TranslationManager.translation("feature.tpa.accept.success"), Prefix.OPENMC, MessageType.SUCCESS, true);
		TPAManager.removeRequest(requester, target);
	}
}
