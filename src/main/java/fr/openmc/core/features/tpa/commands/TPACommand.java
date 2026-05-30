package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TPACommand {
	
	/**
	 * Command to send a teleport request to a player.
	 * @param player The player sending the request.
	 * @param target The target player to whom the request is sent.
	 */
	@Command({"tpa", "tpask"})
	@CommandPermission("omc.commands.tpa")
	public void tpaAsk(
			Player player,
			@Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player target
	) {
		if (TPAManager.requesterHasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous avez déjà une demande de téléportation en attente\n")
					.append(Component.text("§3Tapez §5/tpacancel §3pour annuler votre demande de tp en cours").clickEvent(ClickEvent.runCommand("/tpacancel")).hoverEvent(HoverEvent.showText(Component.text("Annuler la demande de TP")))
					), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§4Le joueur n'existe pas ou n'est pas en ligne"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (player == target) {
			MessagesManager.sendMessage(player, Component.text("§4Vous ne pouvez pas vous envoyer de demande de téléportation à vous même"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAManager.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, Component.text("§4Vous avez déjà une demande de téléportation en attente de votre acceptation"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		sendTPARequest(player, target);
	}
	
	private void sendTPARequest(Player player, Player target) {
		TPAManager.addRequest(player, target);
		
		MessagesManager.sendMessage(target,
				TranslationManager.translation("feature.tpa.request.target_message", Component.text(player.getName()))
						.append(Component.text("\n"))
						.append(TranslationManager.translation("feature.tpa.request.target_accept").clickEvent(ClickEvent.runCommand("/tpaccept")).hoverEvent(HoverEvent.showText(Component.text("Accepter la demande de TP"))))
						.append(Component.text(" "))
						.append(TranslationManager.translation("feature.tpa.request.target_deny").clickEvent(ClickEvent.runCommand("/tpadeny")).hoverEvent(HoverEvent.showText(Component.text("Refuser la demande de TP"))))
				, Prefix.OPENMC, MessageType.INFO, true);
		MessagesManager.sendMessage(player,
				TranslationManager.translation("feature.tpa.request.sender_message", Component.text(target.getName()))
				.append(Component.text("\n"))
				.append(TranslationManager.translation("feature.tpa.request.sender_cancel").clickEvent(ClickEvent.runCommand("/tpacancel")).hoverEvent(HoverEvent.showText(Component.text("Annuler la demande de TP"))))
				, Prefix.OPENMC, MessageType.SUCCESS, true);

		new BukkitRunnable() {
			@Override
			public void run() {
				TPAManager.expireRequest(player, target);
			}
		}.runTaskLater(OMCPlugin.getInstance(), 800);
	}
	
}
