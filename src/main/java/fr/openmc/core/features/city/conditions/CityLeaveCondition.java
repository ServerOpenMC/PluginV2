package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour quitter une ville (utile pour faire une modif sur menu et commandes).
 */
public class CityLeaveCondition {

    /**
     * Retourne un booleen pour dire si le joueur peut quitter
     *
     * @param city la ville sur laquelle on veut quitter
     * @param player le joueur qui veut quitter
     * @return booleen
     */
    public static boolean canCityLeave(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
            MessagesManager.sendMessage(player, Component.text("Tu ne peux pas quitter la ville car tu en es le maire, supprime la ou transfère la propriété"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
