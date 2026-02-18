package fr.openmc.core.features.dream.listeners.strctures;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import fr.openmc.core.features.dream.generation.structures.DreamStructuresManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEnterStructureListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (!DreamUtils.isInDreamWorld(player)) return;
		
		Location from = e.getFrom();
		Location to = e.getTo();
		if ((from.getBlockX() >> 4) != (to.getBlockX() >> 4) || (from.getBlockZ() >> 4) != (to.getBlockZ() >> 4)) return;
		
		DreamStructure oldStructure = DreamStructuresManager.getStructureAt(from);
		DreamStructure newStructure = DreamStructuresManager.getStructureAt(to);
		if (oldStructure == newStructure) return;
		if (newStructure == null) return;
		
		MessagesManager.sendMessage(player, Component.text("§7Vous entrez dans : " + newStructure.type().getName()), Prefix.DREAM, MessageType.INFO, true);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerEnterStructureEvent(player, newStructure));
	}
}
