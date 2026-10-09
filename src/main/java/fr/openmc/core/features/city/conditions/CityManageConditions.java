package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldown;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.CityPermission;
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
    private final static DynamicCooldownManager dynamicCooldownManager = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();

    /**
     * Retourne un booleen pour dire si la ville peut etre rename
     *
     * @param city la ville sur laquelle on modifie le nom
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityRename(City city, OMCPlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.RENAME))) {
            player.message().sendError(TranslationManager.translation("feature.city.conditions.manage.rename.no_permission"), Prefix.CITY, false);
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
    public static boolean canCityTransfer(City city, OMCPlayer player, UUID target) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (target.equals(player.getUniqueId())) {
            player.message().sendError(TranslationManager.translation("feature.city.conditions.manage.transfer.self"), Prefix.CITY, false);
            return false;
        }

        if (city.getPlayerWithPermission(CityPermission.OWNER).equals(target)) {
            player.message().sendError(TranslationManager.translation("feature.city.conditions.manage.transfer.already_owner"), Prefix.CITY, false);
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
    public static boolean canCityTransfer(City city, OMCOfflinePlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.OWNER))) {
	        player.message().sendError(TranslationManager.translation("feature.city.player_isnt_owner"), Prefix.CITY, false);
            return false;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
	        player.message().sendError(TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY, false);
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
    public static boolean canCityDelete(City city, OMCPlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!dynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
            player.message().sendInfo(TranslationManager.translation(
                    "feature.city.conditions.manage.delete.must_wait",
                    Component.text(dynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big") / 1000)
            ), Prefix.CITY, false);
            return false;
        }

        if (city.isInWar()) {
            player.message().sendError(TranslationManager.translation("feature.city.conditions.manage.delete.cant_in_war"), Prefix.CITY, false);
            return false;
        }

        if (!city.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            player.message().sendError(TranslationManager.translation("feature.city.player_isnt_owner"), Prefix.CITY, false);
            return false;
        }
        return true;
    }
}
