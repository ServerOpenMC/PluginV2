package fr.openmc.core.features.tpa.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.tpa.TPAManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
			MessagesManager.sendMessage(player,
					TranslationManager.translation("feature.tpa.already_pending")
							.append(Component.text("\n"))
							.append(TranslationManager.translation("feature.tpa.already_pending_usage")
									.clickEvent(ClickEvent.runCommand("/tpacancel"))
									.hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.tpa.request.hover.cancel")))),
					Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		if (target == null) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.player_not_found"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (player == target) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.cannot_tp_yourself"), Prefix.OPENMC, MessageType.ERROR, false);
			return;
		}
		
		if (TPAManager.hasPendingRequest(player)) {
			MessagesManager.sendMessage(player, TranslationManager.translation("feature.tpa.already_have_pending"), Prefix.OPENMC, MessageType.ERROR, true);
			return;
		}
		
		sendTPARequest(player, target);
	}
	
	private void sendTPARequest(Player player, Player target) {
		TPAManager.addRequest(player, target);
		
		MessagesManager.sendMessage(target,
				TranslationManager.translation("feature.tpa.request.target_message", Component.text(player.getName()).color(NamedTextColor.GOLD))
						.append(Component.text("\n"))
						.append(TranslationManager.translation("feature.tpa.request.target_accept")
								.clickEvent(ClickEvent.runCommand("/tpaccept"))
								.hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.tpa.request.hover.accept"))))
						.append(Component.text(" "))
						.append(TranslationManager.translation("feature.tpa.request.target_deny")
								.clickEvent(ClickEvent.runCommand("/tpadeny"))
								.hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.tpa.request.hover.deny"))))
				, Prefix.OPENMC, MessageType.INFO, true);
		MessagesManager.sendMessage(player,
				TranslationManager.translation("feature.tpa.request.sender_message", Component.text(target.getName()).color(NamedTextColor.GOLD))
						.append(Component.text("\n"))
						.append(TranslationManager.translation("feature.tpa.request.sender_cancel")
								.clickEvent(ClickEvent.runCommand("/tpacancel"))
								.hoverEvent(HoverEvent.showText(TranslationManager.translation("feature.tpa.request.hover.cancel"))))
				, Prefix.OPENMC, MessageType.SUCCESS, true);

		new BukkitRunnable() {
			@Override
			public void run() {
				TPAManager.expireRequest(player, target);
			}
		}.runTaskLater(OMCPlugin.getInstance(), 800);
	}
	
}
