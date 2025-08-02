package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * autour d'un rank (utile pour faire une modif sur menu et commandes).
 */
public class CityRankCondition {

    /**
     * Retourne un booleen pour dire si un rank peut etre créer
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCreateRank(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (!city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (city.getRanks().size() >= City.MAX_RANKS) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_MAX.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si un rank peut etre renommé
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canRenameRank(City city, Player player, String oldRankName) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        CityRank rank = city.getRankByName(oldRankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (city.getRanks().size() >= City.MAX_RANKS) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_MAX.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si un rank peut etre supprimé
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canDeleteRank(City city, Player player, String rankName) {
        if (!city.hasPermission(player.getUniqueId(), CPermission.PERMS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        CityRank rank = city.getRankByName(rankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
