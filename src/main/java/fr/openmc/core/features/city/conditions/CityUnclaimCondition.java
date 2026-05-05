package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour unclaim une zone (utile pour faire une modif sur menu et commandes).
 */
public class CityUnclaimCondition {

    /**
     * Retourne un booleen pour dire si la ville peut etre retrécie ou non.
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityUnclaim(City city, Player player) {
        if (player.getWorld() != Bukkit.getWorld("world")) return false;

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.CLAIM))) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.unclaim.no_permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
