package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour modifier une ville (utile pour faire une modif sur menu et commandes).
 */
public class CityManageConditions {

    /**
     * Retourne un booleen pour dire si la ville peut etre rename
     *
     * @param city la ville sur laquelle on modifie le nom
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityRename(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.RENAME))) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.manage.rename.no_permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    /**
     * Retourne un booleen pour dire si la ville peut etre transferer
     *
     * @param city la ville sur laquelle on modifie le propriétaire
     * @param player le joueur sur lequel tester les permissions
     * @param target le joueur cible vers lequel on veut transferer la ville
     * @return booleen
     */
    public static boolean canCityTransfer(City city, Player player, UUID target) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (target.equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.manage.transfer.self"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getPlayerWithPermission(CityPermission.OWNER).equals(target)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.manage.transfer.already_owner"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return canCityTransfer(city, player);
    }

    /**
     * Retourne un booleen pour dire si la ville peut etre transferer
     *
     * @param city la ville sur laquelle on modifie le propriétaire
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityTransfer(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.OWNER))) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.player_isnt_owner"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    /**
     * Retourne un booleen pour dire si la ville peut etre delete
     *
     * @param city la ville sur laquelle on veut delete
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityDelete(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.manage.delete.must_wait",
                    Component.text(DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big") / 1000)
            ), Prefix.CITY, MessageType.INFO, false);
            return false;
        }

        if (city.isInWar()) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.manage.delete.cant_in_war"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.player_isnt_owner"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
