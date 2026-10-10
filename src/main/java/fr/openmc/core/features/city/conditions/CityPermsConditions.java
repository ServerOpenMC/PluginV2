package fr.openmc.core.features.city.conditions;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;

import java.util.Objects;
import java.util.UUID;

public class CityPermsConditions {
    public static boolean canSeePerms(OMCPlayer sender, UUID playerUUID) {
        City city = City.ofPlayer(playerUUID);
        City senderCity = sender.city().getCity();

        if (senderCity == null) {
            sender.message().sendError(TranslationManager.translation("messages.city.target_no_city"), Prefix.CITY, false);
            return false;
        }

        if (city == null) {
            sender.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!Objects.equals(senderCity.getUniqueId(), city.getUniqueId())) {
            sender.message().sendError(TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY,false);
            return false;
        }

        if (!city.getMembers().contains(playerUUID)) {
            sender.message().sendError(TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY, false);
            return false;
        }

        if (city.hasPermission(playerUUID, CityPermission.OWNER)) {
            sender.message().sendError(TranslationManager.translation("feature.city.player_is_owner"), Prefix.CITY, false);
            return false;
        }

        return true;
    }

    public static boolean canModifyPerms(OMCPlayer sender, CityPermission permission) {
        City city = sender.city().getCity();

        if (permission == CityPermission.OWNER) {
            sender.message().sendError(TranslationManager.translation("feature.city.cant_do_this"), Prefix.CITY, false);
            return false;
        }

        if (city == null) {
            sender.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CityPermission.MANAGE_PERMS))) {
            sender.message().sendError(TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, false);
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CityPermission.MANAGE_PERMS) {
            sender.message().sendError(TranslationManager.translation("feature.city.only_owner_can_do_this"), Prefix.CITY, false);
            return false;
        }

        return true;
    }
}
