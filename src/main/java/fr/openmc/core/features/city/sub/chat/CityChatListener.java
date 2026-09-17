package fr.openmc.core.features.city.sub.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CityChatListener implements Listener {
	
	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Component message = event.message();
		
		if (CityChatManager.isCityChatMember(player)) {
			event.setCancelled(true);
			CityChatManager.sendCityChatMessage(player, message);
		}
	}
}
