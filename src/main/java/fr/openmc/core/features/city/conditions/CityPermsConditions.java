package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class CityPermsConditions {
    public static boolean canSeePerms(Player sender, UUID playerUUID) {
        City city = CityManager.getPlayerCity(playerUUID);
        City senderCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (senderCity == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!Objects.equals(senderCity.getUniqueId(), city.getUniqueId())) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(playerUUID)) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.TARGET_IN_OTHER_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(playerUUID, CityPermission.OWNER)) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.CITY_IS_OWNER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    public static boolean canModifyPerms(Player sender, CityPermission permission) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CityPermission.PERMS))) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CityPermission.PERMS) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.CITY_ONLY_OWNER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
