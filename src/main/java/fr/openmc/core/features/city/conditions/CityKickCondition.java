package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions neccessaires
 * pour kick une personne (utile pour faire une modif sur menu et commandes)
 */
public class CityKickCondition {

    /**
     * Retourne un booleen pour dire si le joueur peut etre kick
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @param playerToKick le joueur sur lequel tester si il peut etre kick
     * @return booleen
     */
    public static boolean canCityKickPlayer(City city, Player player, OfflinePlayer playerToKick) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (player.getUniqueId().equals(playerToKick.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Tu ne peux pas t'auto exclure de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.KICK))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission d'exclure un membre"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(playerToKick.getUniqueId(), CPermission.KICK))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission d'exclure " + playerToKick.getName()), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(playerToKick.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessage(player, Component.text("Tu ne peux pas exclure le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
