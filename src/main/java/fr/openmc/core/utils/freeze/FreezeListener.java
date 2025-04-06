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
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (FreezeManager.FROZEN_PLAYERS.contains(player)) {
			Date banDuration = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
			Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Déconnexion en étant freeze !", banDuration, "Anti Déco Freeze");
			FreezeManager.FROZEN_PLAYERS.remove(player);
			
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (FreezeManager.FROZEN_PLAYERS.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof Player && FreezeManager.FROZEN_PLAYERS.contains(((Player) entity).getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if (FreezeManager.FROZEN_PLAYERS.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
}
