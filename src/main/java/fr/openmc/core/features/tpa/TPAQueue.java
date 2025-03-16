package fr.openmc.core.features.tpa;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TPAQueue {
	
	public static final TPAQueue QUEUE = new TPAQueue();
	
	private final ConcurrentHashMap<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, Long> tpaRequestTime = new ConcurrentHashMap<>();
	
	public boolean hasPendingRequest(Player player) {
		return tpaRequests.containsKey(player.getUniqueId());
	}
	
	public void addRequest(Player player, Player target) {
		tpaRequests.put(target.getUniqueId(), player.getUniqueId());
		tpaRequestTime.put(player.getUniqueId(), System.currentTimeMillis());
	}
	
	public void expireRequest(Player player, Player target) {
		if (tpaRequests.containsKey(target.getUniqueId())) {
			long requestTime = tpaRequestTime.get(player.getUniqueId());
			if (System.currentTimeMillis() - requestTime >= 30000) { // 30 secondes
				MessagesManager.sendMessage(player, Component.text("§4Votre demande de téléportation à §6" + target.getName() + " §4a expiré"), Prefix.OPENMC, MessageType.WARNING, true);
				MessagesManager.sendMessage(target, Component.text("§3La demande de téléportation de §6" + player.getName() + " §4a expiré"), Prefix.OPENMC, MessageType.INFO, true);
				
				removeRequest(target);
			}
		}
	}
	
	public Player getRequester(Player target) {
		UUID requesterUUID = tpaRequests.get(target.getUniqueId());
		return requesterUUID == null ? null : target.getServer().getPlayer(requesterUUID);
	}
	
	public void removeRequest(Player target) {
		tpaRequests.remove(target.getUniqueId());
	}
}
