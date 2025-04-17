package fr.openmc.core.utils.freeze;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
			Date banDuration = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
			Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Déconnexion en étant freeze !", banDuration, "Anti Déco Freeze");
			FreezeManager.FROZEN_PLAYERS.remove(player);
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
		if (entity instanceof Player && FreezeManager.FROZEN_PLAYERS.contains(((Player) entity).getPlayer())) {
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
