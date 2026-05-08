package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class CityPermsConditions {
    public static boolean canSeePerms(Player sender, UUID playerUUID) {
        City city = CityManager.getPlayerCity(playerUUID);
        City senderCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (senderCity == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.target_no_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!Objects.equals(senderCity.getUniqueId(), city.getUniqueId())) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(playerUUID)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.target_in_other_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(playerUUID, CityPermission.OWNER)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.player_is_owner"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    public static boolean canModifyPerms(Player sender, CityPermission permission) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CityPermission.MANAGE_PERMS))) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CityPermission.MANAGE_PERMS) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.city.only_owner_can_do_this"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
