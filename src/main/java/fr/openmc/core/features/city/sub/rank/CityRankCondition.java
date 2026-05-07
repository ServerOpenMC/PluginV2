package fr.openmc.core.features.city.sub.rank;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
              MessagesManager.sendMessage(player, TranslationManager.translation(
                  "messages.city.havent_unlocked_feature",
                  Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK)).color(NamedTextColor.GOLD)
              ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (city.getRanks().size() >= RankLimitRewards.getRankLimit(city.getLevel())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.grade.max_reach"), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
              MessagesManager.sendMessage(player, TranslationManager.translation(
                  "messages.city.havent_unlocked_feature",
                  Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK)).color(NamedTextColor.GOLD)
              ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        DBCityRank rank = city.getRankByName(oldRankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        if (!canModifyRankPermissions(city, player, rank.getPriority())) {
            return false;
        }
        if (city.getRanks().size() >= RankLimitRewards.getRankLimit(city.getLevel())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.grade.max_reach"), Prefix.CITY, MessageType.ERROR, false);
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
              MessagesManager.sendMessage(player, TranslationManager.translation(
                  "messages.city.havent_unlocked_feature",
                  Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK)).color(NamedTextColor.GOLD)
              ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_PERMS)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        
        DBCityRank rank = city.getRankByName(rankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return canModifyRankPermissions(city, player, rank.getPriority());
    }
    
    public static boolean canModifyRankPermissions(City city, Player player, int rankPriority) {
        if (city.getRankOfMember(player.getUniqueId()) == null) return true;
        
        if (city.getRankOfMember(player.getUniqueId()).getPriority() >= rankPriority) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.grade.cannot_modify_sup_role"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
