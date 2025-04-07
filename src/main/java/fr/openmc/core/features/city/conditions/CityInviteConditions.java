package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static fr.openmc.core.features.city.commands.CityCommands.invitations;

/**
 * Le but de cette classe est de regrouper toutes les conditions neccessaires
 * pour inviter une personne (utile pour faire une modif sur menu et commandes)
 */
public class CityInviteConditions {

    /**
     * Retourne un booleen pour dire si le joueur peut etre invité
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @param target le joueur sur lequel tester si il peut etre inviter
     * @return booleen
     */
    public static boolean canCityInvitePlayer(City city, Player player, Player target) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CPermission.INVITE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission d'inviter des joueurs dans la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (CityManager.getPlayerCity(target.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, Component.text("Cette personne est déjà dans une ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (invitations.containsKey(target)) {
            MessagesManager.sendMessage(player, Component.text("Cette personne as déjà une invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut refuser l'invitation
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityInviteDeny(Player player) {
        if (!invitations.containsKey(player)) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut etre invité
     *
     * @param newCity la ville sur laquelle on fait les actions
     * @param inviter le joueur sur lequel tester les permissions
     * @param playerInvited le joueur sur lequel tester si il peut etre inviter
     * @return booleen
     */
    public static boolean canCityInviteAccept(City newCity, Player inviter, Player playerInvited) {
        if (!invitations.containsKey(playerInvited)) {
            MessagesManager.sendMessage(playerInvited, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (newCity == null) {
            MessagesManager.sendMessage(inviter, Component.text("L'invitation a expiré"), Prefix.CITY, MessageType.SUCCESS, false);
            return false;
        }

        return true;
    }
}
