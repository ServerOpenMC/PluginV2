package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour modifier une ville (utile pour faire une modif sur menu et commandes).
 */
public class CityManageConditions {


    /**
     * Retourne un booleen pour dire si la ville peut etre rename
     *
     * @param city la ville sur laquelle on modifie le nom
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityRename(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.RENAME))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de renommer ta ville."), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si la ville peut etre transferer
     *
     * @param city la ville sur laquelle on modifie le propriétaire
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityTransfer(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.OWNER))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'es pas le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Ce joueur n'habite pas dans votre ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si la ville peut etre delete
     *
     * @param city la ville sur laquelle on veut delete
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityDelete(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
            MessagesManager.sendMessage(player, Component.text("§cTu dois attendre avant de pouvoir supprimer ta ville ("+ DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big")/1000 + " secondes)"), Prefix.CITY, MessageType.INFO, false);
            return false;
        }

        if (!city.getPlayerWith(CPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Tu n'es pas le maire de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }
}
