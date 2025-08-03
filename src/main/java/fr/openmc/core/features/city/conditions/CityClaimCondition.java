package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour claim une zone (utile pour faire une modif sur menu et commandes).
 */
public class CityClaimCondition {

    /**
     * Retourne un booleen pour dire si la ville peut etre etendu
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityClaim(City city, Player player) {
        if (player.getWorld() != Bukkit.getWorld("world")) return false ;
        
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.CLAIM))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de claim"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
