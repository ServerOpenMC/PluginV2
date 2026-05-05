package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(player.getUniqueId(), CityPermission.OWNER)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.leave.owner_cant_leave"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
