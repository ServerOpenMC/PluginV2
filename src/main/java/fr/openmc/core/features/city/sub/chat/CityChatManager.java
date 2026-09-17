package fr.openmc.core.features.city.sub.chat;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CityChatManager extends Feature implements HasCommands, HasListeners {

	@Override
	public Set<Object> getCommands() {
		return Set.of(
				new CityChatCommand()
		);
	}

	@Override
	public Set<ListenerFactory> getListeners() {
		return Set.of(
				CityChatListener::new
		);
	}

	/**
	 * Liste des joueurs dans le chat de ville
	 */
	public final List<Player> cityChatMembers = new ArrayList<>();
	
	/**
	 * Ajoute un joueur au chat de ville
	 * @param player Le joueur à ajouter
	 */
	public void addCityChatMember(Player player) {
		cityChatMembers.add(player);
		MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.chat.entered"),
				Prefix.CITY, MessageType.INFO, false);
	}
	
	/**
	 * Retire un joueur du chat de ville
	 * @param player Le joueur à retirer
	 */
	public void removeCityChatMember(Player player) {
		cityChatMembers.remove(player);
		MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.chat.leaved"),
				Prefix.CITY, MessageType.INFO, false);
	}
	
	/**
	 * Vérifie si un joueur est dans le chat de ville
	 * @param player Le joueur à vérifier
	 * @return true si le joueur est dans le chat de ville, false sinon
	 */
	public boolean isCityChatMember(Player player) {
		return cityChatMembers.contains(player);
	}
	
	/**
	 * Envoie un message au chat de ville
	 * @param sender Le joueur qui envoie le message
	 * @param message Le message à envoyer
	 */
	public void sendCityChatMessage(Player sender, Component message) {
		City city = City.ofPlayer(sender.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.player_no_in_city"),
					Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		Component msg_component = TranslationManager.translation("feature.city.chat.prefix").color(NamedTextColor.GOLD)
				.appendSpace()
				.append(sender.displayName().color(NamedTextColor.WHITE)).append(
				Component.text(" » ").color(NamedTextColor.GRAY).append(
						message.color(NamedTextColor.WHITE)
				)
		);
		
		for (UUID uuid : city.getMembers()) {
			OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(uuid);
			if (offlinePlayer.isOnline() && offlinePlayer instanceof Player player) {
				player.sendMessage(msg_component);
			}
		}
	}
}
