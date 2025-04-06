package fr.openmc.core.utils.freeze;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeListener implements Listener {

	public void onPlayerMove(PlayerMoveEvent e) {
		if (FreezeManager.FROZEN_PLAYERS.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
}
