package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions neccessaires
 * touchant au mascottes (utile pour faire une modif sur menu et commandes)
 */
public class CityTypeConditions {

    /**
     * Retourne un booleen pour dire si la ville peut changer de typê
     *
     * @param city la ville sur laquelle on test cela
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityChangeType(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.TYPE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de changer le status de ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

}
