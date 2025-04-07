package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static fr.openmc.core.features.city.commands.CityCommands.balanceCooldownTasks;

/**
 * Le but de cette classe est de regrouper toutes les conditions neccessaires
 * pour tout ce qui est autour de la banque (utile pour faire une modif sur menu et commandes)
 */
public class CityBankConditions {

    /**
     * Retourne un booleen pour dire si le joueur peut donner de l'argent a sa ville
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityDeposit(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_GIVE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (balanceCooldownTasks.containsKey(city.getUUID())){
            MessagesManager.sendMessage(player, Component.text("Ta ville a été attaquer tu n'as donc pas accès à la banque de ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut voir la balance de sa ville
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityBalance(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_BALANCE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de consulter l'argent de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut etre invité
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityWithdraw(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.MONEY_TAKE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de prendre de l'argent de ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (balanceCooldownTasks.containsKey(city.getUUID())){
            MessagesManager.sendMessage(player, Component.text("Ta ville a été attaquer tu n'as donc pas accès à la banque de vlle"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
