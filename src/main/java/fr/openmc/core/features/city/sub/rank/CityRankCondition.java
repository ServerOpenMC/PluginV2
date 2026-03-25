package fr.openmc.core.features.city.sub.rank;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette Feature ! Veuillez Améliorer votre Ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (city.getRanks().size() >= RankLimitRewards.getRankLimit(city.getLevel())) {
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

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette Feature ! Veuillez Améliorer votre Ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + "!"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        DBCityRank rank = city.getRankByName(oldRankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (!canModifyRankPermissions(city, player, rank.getPriority())) {
            return false;
        }
        if (city.getRanks().size() >= RankLimitRewards.getRankLimit(city.getLevel())) {
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
        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + " !"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.PERMS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        
        DBCityRank rank = city.getRankByName(rankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return canModifyRankPermissions(city, player, rank.getPriority());
    }
    
    public static boolean canModifyRankPermissions(City city, Player player, int rankPriority) {
        if (city.getRankOfMember(player.getUniqueId()) == null) return true;
        
        if (city.getRankOfMember(player.getUniqueId()).getPriority() >= rankPriority) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_CANNOT_MODIFY_HIGHER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
