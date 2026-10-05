package fr.openmc.core.features.tpa;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.features.tpa.commands.TPACancelCommand;
import fr.openmc.core.features.tpa.commands.TPACommand;
import fr.openmc.core.features.tpa.commands.TPADenyCommand;
import fr.openmc.core.features.tpa.commands.TPAcceptCommand;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Credit(developers = {"gab400"})
public class TPAManager extends Feature implements HasCommands {
	
	/**
	 * Map to store teleport requests
	 * The key is the target player's UUID, and the value is a list of requesters' UUIDs
	 */
	private static final ConcurrentHashMap<UUID, List<UUID>> tpaRequests = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<UUID, Long> tpaRequestTime = new ConcurrentHashMap<>();

	@Override
	public Set<Object> getCommands() {
		return Set.of(
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
	public static boolean hasPendingRequest(OMCPlayer target) {
		return tpaRequests.get(target.getUniqueId()) != null && !tpaRequests.get(target.getUniqueId()).isEmpty();
	}
	
	/**
	 * Check if the requester has a pending teleport request
	 * @param player The player to check
	 * @return true if the requester has a pending request, false otherwise
	 */
	public static boolean requesterHasPendingRequest(OMCPlayer player) {
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
	public static boolean hasMultipleRequests(OMCPlayer target) {
		List<UUID> requesters = tpaRequests.get(target.getUniqueId());
		return requesters != null && requesters.size() > 1;
	}
	
	/**
	 * Add a teleport request to the queue
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void addRequest(OMCPlayer player, OMCPlayer target) {
		tpaRequests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(player.getUniqueId());
		tpaRequestTime.put(player.getUniqueId(), System.currentTimeMillis());
	}
	
	/**
	 * Expire a teleport request if it exceeds the time limit
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void expireRequest(OMCPlayer player, OMCPlayer target) {
		if (tpaRequests.containsKey(target.getUniqueId())) {
			if (tpaRequests.get(target.getUniqueId()).contains(player.getUniqueId())) {
				long requestTime = tpaRequestTime.get(player.getUniqueId());
				if (System.currentTimeMillis() - requestTime >= 30000) {
					player.message().send(TranslationManager.translation(
							"feature.tpa.expire.sender",
							target.getNameWithHead().color(NamedTextColor.GOLD)
					), Prefix.OPENMC, MessageType.WARNING, true);
					target.message().send(TranslationManager.translation(
							"feature.tpa.expire.target",
							player.getNameWithHead().color(NamedTextColor.GOLD)
					), Prefix.OPENMC, MessageType.INFO, true);

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
	public static List<OMCPlayer> getRequesters(OMCPlayer target) {
		List<OMCPlayer> requesters = new ArrayList<>();
		for (UUID playerUUID : tpaRequests.get(target.getUniqueId())) {
			requesters.add(OMCPlayer.of(playerUUID));
		}
		return requesters;
	}
	
	/**
	 * Remove the teleport request for the target player
	 * @param player The player who sent the request
	 * @param target The target player
	 */
	public static void removeRequest(OMCPlayer player, OMCPlayer target) {
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
	public static OMCPlayer getTargetByRequester(OMCPlayer requester) {
		for (UUID targetUUID : tpaRequests.keySet()) {
			if (tpaRequests.get(targetUUID).contains(requester.getUniqueId())) {
				return OMCPlayer.of(targetUUID);
			}
		}
		return null;
	}
}
