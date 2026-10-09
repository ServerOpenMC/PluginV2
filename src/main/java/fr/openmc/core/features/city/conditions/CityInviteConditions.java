package fr.openmc.core.features.city.conditions;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.CityInvite;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour inviter une personne (utile pour faire une modif sur menu et commandes).
 */
public class CityInviteConditions {
    private static final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();

    /**
     * Retourne un booleen pour dire si le joueur peut etre invité
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @param target le joueur sur lequel tester s'il peut etre inviter
     * @return booleen
     */
    public static boolean canCityInvitePlayer(City city, OMCPlayer player, OMCPlayer target) {
        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (city == null) {
            player.message().sendError(
                    TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY
            );
            return false;
        }

        if (!(city.hasPermission(playerUUID, CityPermission.INVITE))) {
            player.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.no_permission"), Prefix.CITY
            );
            return false;
        }

        if (playerUUID.equals(targetUUID)) {
            player.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.self"), Prefix.CITY
            );
            return false;
        }


        if (City.ofPlayer(targetUUID) != null) {
            player.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.target_already_in_city"), Prefix.CITY
            );
            return false;
        }

        if (!player.settings().canReceiveCityInvite(targetUUID)) {
            player.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.target_cant_receive"), Prefix.CITY
            );
            return false;
        }

        if (cityManager.hasInvitation(targetUUID, playerUUID)) {
            player.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.already_invited"), Prefix.CITY
            );
            return false;
        }

        if (city.getMembers().size() >= MemberLimitRewards.getMemberLimit(city.getLevel())) {
            player.message().sendError(
                    TranslationManager.translation(
                            "feature.city.conditions.invite.member_limit_reached",
                            Component.text(MemberLimitRewards.getMemberLimit(city.getLevel()))
                    ),
                    Prefix.CITY
            );
            return false;
        }

        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut refuser l'invitation
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityInviteDeny(OMCPlayer player, OMCOfflinePlayer inviter) {
        return hasPendingInvitation(player, inviter);
    }

    /**
     * Retourne un booleen pour dire si le joueur peut etre invité
     *
     * @param invited       le joueur qui est invité
     * @param inviter       le joueur qui invite
     * @return booleen
     */
    public static boolean canCityInviteAccept(OMCPlayer invited, OMCOfflinePlayer inviter) {
        if (!hasPendingInvitation(invited, inviter)) return false;

        CityInvite invite = cityManager.getInvitation(invited.getUniqueId(), inviter.getUniqueId());

        City city = invite.city();

        if (city == null) {
            invited.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.expired"), Prefix.CITY, false);
            return false;
        }

        if (City.ofPlayer(invited.getUniqueId()) != null) {
            invited.message().sendError(
                    TranslationManager.translation("messages.city.player_already_in_city"), Prefix.CITY, false);
            return false;
        }

        if (city.getMembers().size() >= MemberLimitRewards.getMemberLimit(city.getLevel())) {
            invited.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.city_member_limit_reached"), Prefix.CITY, false);
            cityManager.removeInvitation(invited.getUniqueId(), inviter.getUniqueId());
            return false;
        }


        return true;
    }

    private static boolean hasPendingInvitation(OMCPlayer invited, OMCOfflinePlayer inviter) {
        UUID invitedUUID = invited.getUniqueId();

        if (!cityManager.hasInvitation(invitedUUID)) {
            invited.message().sendError(TranslationManager.translation("feature.city.invite.commands.accept.none_pending"), Prefix.CITY, false);
            return false;
        }

        if (!cityManager.hasInvitation(invitedUUID, inviter.getUniqueId())) {
            invited.message().sendError(
                    TranslationManager.translation(
                            "feature.city.invite.commands.accept.not_invited",
                            inviter.getNameWithHead()
                    ),
                    Prefix.CITY
            );
            return false;
        }

        return true;
    }
}
