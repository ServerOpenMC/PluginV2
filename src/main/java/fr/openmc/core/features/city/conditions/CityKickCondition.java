package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour kick une personne (utile pour faire une modif sur menu et commandes).
 */
public class CityKickCondition {

    /**
     * Retourne un booleen pour dire si le joueur peut etre kick
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @param playerToKick le joueur sur lequel tester s'il peut etre kick
     * @return booleen
     */
    public static boolean canCityKickPlayer(City city, Player player, OfflinePlayer playerToKick) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (player.getUniqueId().equals(playerToKick.getUniqueId())) {
	        MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_KICK_HIMSELF.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.KICK))) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_KICK.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(playerToKick.getUniqueId(), CityPermission.OWNER)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_KICK.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        
        if (city.getRankOfMember(player.getUniqueId()).getPriority() <= city.getRankOfMember(playerToKick.getUniqueId()).getPriority()) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_KICK.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
