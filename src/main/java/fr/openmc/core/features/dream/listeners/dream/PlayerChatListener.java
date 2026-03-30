package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {
	
	@EventHandler
	public void onPlayerChat(AsyncChatEvent e) {
		if (e.isCancelled()) return;
		
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		e.setCancelled(true);
	}
}
