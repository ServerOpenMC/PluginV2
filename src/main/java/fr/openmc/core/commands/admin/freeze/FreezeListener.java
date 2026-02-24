package fr.openmc.core.commands.admin.freeze;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FreezeListener implements Listener {
	
	/**
	 * When a player disconnects, if he is frozen, we ban him for 30 days
	 *
	 * @param event PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (FreezeManager.FROZEN_PLAYERS.contains(player)) {
			if (event.getReason() != PlayerQuitEvent.QuitReason.DISCONNECTED) {
				FreezeManager.contactFreezer(event.getReason());
			}
		}
	}
	
	/**
	 * When a player joins, if he is frozen, we set him invulnerable and send him a message
	 *
	 * @param event PlayerJoinEvent
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (FreezeManager.FROZEN_PLAYERS.contains(player)) {
			player.setInvulnerable(true);
			player.sendTitlePart(TitlePart.TITLE, Component.translatable("command.admin.freeze.title.1"));
			player.sendTitlePart(TitlePart.SUBTITLE, Component.translatable("command.admin.freeze.title.2"));
			MessagesManager.sendMessage(player, Component.translatable("command.admin.freeze.player_freezed"), Prefix.OPENMC, MessageType.WARNING, true);
		}
	}

	/**
	 * When a player moves, if he is frozen, we cancel the event
	 *
	 * @param event PlayerMoveEvent
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (FreezeManager.FROZEN_PLAYERS.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * When a player is damaged, if he is frozen, we cancel the event
	 *
	 * @param event EntityDamageEvent
	 */
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player player && FreezeManager.FROZEN_PLAYERS.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * When a player teleports, if he is frozen, we cancel the event
	 *
	 * @param event PlayerTeleportEvent
	 */
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (FreezeManager.FROZEN_PLAYERS.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
}
