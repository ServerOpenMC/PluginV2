package fr.openmc.core.features.city;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CityChatManager {
	
	/**
	 * Liste des joueurs dans le chat de ville
	 */
	public static List<Player> cityChatMembers = new ArrayList<>();
	
	/**
	 * Ajoute un joueur au chat de ville
	 * @param player Le joueur à ajouter
	 */
	public static void addCityChatMember(Player player) {
		cityChatMembers.add(player);
		MessagesManager.sendMessage(player, Component.text("Vous avez rejoint le chat de ville"), Prefix.CITY, MessageType.INFO, false);
	}
	
	/**
	 * Retire un joueur du chat de ville
	 * @param player Le joueur à retirer
	 */
	public static void removeCityChatMember(Player player) {
		cityChatMembers.remove(player);
		MessagesManager.sendMessage(player, Component.text("Vous avez quitté le chat de ville"), Prefix.CITY, MessageType.INFO, false);
	}
	
	/**
	 * Vérifie si un joueur est dans le chat de ville
	 * @param player Le joueur à vérifier
	 * @return true si le joueur est dans le chat de ville, false sinon
	 */
	public static boolean isCityChatMember(Player player) {
		return cityChatMembers.contains(player);
	}
	
	/**
	 * Envoie un message au chat de ville
	 * @param sender Le joueur qui envoie le message
	 * @param message Le message à envoyer
	 */
	public static void sendCityChatMessage(Player sender, Component message) {
		City city = CityManager.getPlayerCity(sender.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		Component msg_component = Component.text("#ville ").color(NamedTextColor.GOLD).append(sender.displayName().color(NamedTextColor.WHITE)).append(
				Component.text(" » ").color(NamedTextColor.GRAY).append(
						message.color(NamedTextColor.WHITE)
				)
		);
		
		for (UUID uuid : city.getMembers()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if (player.isOnline()) {
				((Player) player).sendMessage(msg_component);
			}
		}
	}
}
