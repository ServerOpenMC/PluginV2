package fr.openmc.core.features.city.sub.chat;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CityChatListener implements Listener {
	private final CityChatManager cityChatManager = OMCRegistry.CITY_FEATURES.CITY_CHAT;

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Component message = event.message();
		
		if (cityChatManager.isCityChatMember(player)) {
			event.setCancelled(true);
			cityChatManager.sendCityChatMessage(player, message);
		}
	}
}
