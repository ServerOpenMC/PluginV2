package fr.openmc.core.features.city.conditions;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.CityInvite;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

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
     * @param inviter le joueur sur lequel tester les permissions
     * @param invited le joueur sur lequel tester s'il peut etre inviter
     * @return booleen
     */
    public static boolean canCityInvitePlayer(City city, OMCPlayer inviter, OMCPlayer invited) {
        UUID inviterUUID = inviter.getUniqueId();
        UUID invitedUUID = invited.getUniqueId();

        if (city == null) {
            inviter.message().sendError(
                    TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY
            );
            return false;
        }

        if (!(city.hasPermission(inviterUUID, CityPermission.INVITE))) {
            inviter.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.no_permission"), Prefix.CITY
            );
            return false;
        }

        if (inviterUUID.equals(invitedUUID)) {
            inviter.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.self"), Prefix.CITY
            );
            return false;
        }


        if (City.ofPlayer(invitedUUID) != null) {
            inviter.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.target_already_in_city"), Prefix.CITY
            );
            return false;
        }

        if (!inviter.settings().canReceiveCityInvite(invitedUUID)) {
            inviter.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.target_cant_receive"), Prefix.CITY
            );
            return false;
        }

        if (cityManager.hasInvitation(invitedUUID, inviterUUID)) {
            inviter.message().sendError(
                    TranslationManager.translation("feature.city.conditions.invite.already_invited"), Prefix.CITY
            );
            return false;
        }

        if (city.getMembers().size() >= MemberLimitRewards.getMemberLimit(city.getLevel())) {
            inviter.message().sendError(
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
     * @param invited le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityInviteDeny(OMCPlayer invited, OMCOfflinePlayer inviter) {
        return hasPendingInvitation(invited, inviter);
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

    /**
     * Retourne un booleen pour dire si le joueur peut annuler une invitation
     *
     * @param invited       le joueur qui est invité
     * @param inviter       le joueur qui invite
     * @return booleen
     */
    public static boolean canCityInviteCancel(OMCOfflinePlayer invited, OMCPlayer inviter) {
        return hasPendingInvitation(invited, inviter);
    }

    private static boolean hasPendingInvitation(OMCOfflinePlayer invited, OMCOfflinePlayer inviter) {
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
