package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class CityPermsConditions {
    public static boolean canSeePerms(Player sender, UUID playerUUID) {
        City city = CityManager.getPlayerCity(playerUUID);
        City senderCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (senderCity == null) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'habite aucune ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!Objects.equals(senderCity.getUniqueId(), city.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'habite pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(playerUUID)) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(playerUUID, CityPermission.OWNER)) {
            MessagesManager.sendMessage(sender, Component.text("Le propriétaire a les pleins pouvoirs"), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission de gérer les permissions"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CityPermission.PERMS) {
            MessagesManager.sendMessage(sender, Component.text("Seul le propriétaire peut modifier cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
