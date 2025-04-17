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
	
	/**
	 * Check if the player has a pending teleport request
	 * @param player The player to check
	 * @return true if the player has a pending request, false otherwise
	 */
	public boolean hasPendingRequest(Player player) {
		return tpaRequests.containsKey(player.getUniqueId());
	}
	
	/**
	 * Check if the player is the requester of a teleport request
	 * @param player The player to check
	 * @return true if the player is the requester, false otherwise
	 */
	public void addRequest(Player player, Player target) {
		tpaRequests.put(target.getUniqueId(), player.getUniqueId());
		tpaRequestTime.put(player.getUniqueId(), System.currentTimeMillis());
	}
	
	/**
	 * Check if the player is the target of a teleport request
	 * @param player The player to check
	 * @return true if the player is the target, false otherwise
	 */
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
	
	/**
	 * Get the player who sent the teleport request
	 * @param target The target player
	 * @return The requester player, or null if not found
	 */
	public Player getRequester(Player target) {
		UUID requesterUUID = tpaRequests.get(target.getUniqueId());
		return requesterUUID == null ? null : target.getServer().getPlayer(requesterUUID);
	}
	
	/**
	 * Remove the teleport request for the target player
	 * @param target The target player
	 */
	public void removeRequest(Player target) {
		tpaRequests.remove(target.getUniqueId());
	}
}
