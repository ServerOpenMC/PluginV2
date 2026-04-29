package fr.openmc.core.features.tpa;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.tpa.commands.TPACancelCommand;
import fr.openmc.core.features.tpa.commands.TPACommand;
import fr.openmc.core.features.tpa.commands.TPADenyCommand;
import fr.openmc.core.features.tpa.commands.TPAcceptCommand;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TPAQueue {
	
	/**
	 * Map to store teleport requests
	 * The key is the target player's UUID, and the value is a list of requesters' UUIDs
	 */
	private static final ConcurrentHashMap<UUID, List<UUID>> tpaRequests = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, Long> tpaRequestTime = new ConcurrentHashMap<>();

    public static void initCommand() {
        CommandsManager.getHandler().register(
                new TPAcceptCommand(),
                new TPACommand(),
                new TPADenyCommand(),
                new TPACancelCommand()
        );
    }

	/**
	 * Check if the player has a pending teleport request
	 * @param target The player to check
	 * @return true if the player has a pending request, false otherwise
	 */
	public static boolean hasPendingRequest(Player target) {
		return tpaRequests.get(target.getUniqueId()) != null && !tpaRequests.get(target.getUniqueId()).isEmpty();
	}
	
	/**
	 * Check if the requester has a pending teleport request
	 * @param player The player to check
	 * @return true if the requester has a pending request, false otherwise
	 */
	public static boolean requesterHasPendingRequest(Player player) {
		for (List<UUID> requesters : tpaRequests.values()) {
			if (requesters.contains(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the target player has multiple requests
	 * @param target The target player
	 * @return true if the target has multiple requests, false otherwise
	 */
	public static boolean hasMultipleRequests(Player target) {
		List<UUID> requesters = tpaRequests.get(target.getUniqueId());
		return requesters != null && requesters.size() > 1;
	}
	
	/**
	 * Add a teleport request to the queue
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void addRequest(Player player, Player target) {
		tpaRequests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(player.getUniqueId());
		tpaRequestTime.put(player.getUniqueId(), System.currentTimeMillis());
	}
	
	/**
	 * Expire a teleport request if it exceeds the time limit
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void expireRequest(Player player, Player target) {
		if (tpaRequests.containsKey(target.getUniqueId())) {
			if (tpaRequests.get(target.getUniqueId()).contains(player.getUniqueId())) {
				long requestTime = tpaRequestTime.get(player.getUniqueId());
				if (System.currentTimeMillis() - requestTime >= 30000) { // 30 secondes
					MessagesManager.sendMessage(player, Component.text("§4Votre demande de téléportation à §6" + target.getName() + " §4a expiré"), Prefix.OPENMC, MessageType.WARNING, true);
					MessagesManager.sendMessage(target, Component.text("§3La demande de téléportation de §6" + player.getName() + " §4a expiré"), Prefix.OPENMC, MessageType.INFO, true);
					
					removeRequest(player, target);
				}
			}
		}
	}
	
	/**
	 * Get the requesters for a target player
	 * @param target The target player
	 * @return List of players who sent requests to the target player, or null if none
	 */
	public static List<Player> getRequesters(Player target) {
		List<Player> requesters = new ArrayList<>();
		for (UUID playerUUID : tpaRequests.get(target.getUniqueId())) {
			requesters.add(Bukkit.getServer().getPlayer(playerUUID));
		}
		return requesters;
	}
	
	/**
	 * Remove the teleport request for the target player
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void removeRequest(Player player, Player target) {
		tpaRequests.compute(target.getUniqueId(), (key, requesters) -> {
			if (requesters != null) {
				requesters.remove(player.getUniqueId());
				if (requesters.isEmpty()) {
					return null; // Supprimer la clé si la liste est vide
				}
			}
			return requesters;
		});
	}
	
	/**
	 * Get the target player by the requester
	 * @param requester The requester player
	 * @return The target player, or null if not found
	 */
	public static Player getTargetByRequester(Player requester) {
		for (UUID targetUUID : tpaRequests.keySet()) {
			if (tpaRequests.get(targetUUID).contains(requester.getUniqueId())) {
				return Bukkit.getServer().getPlayer(targetUUID);
			}
		}
		return null;
	}
}
