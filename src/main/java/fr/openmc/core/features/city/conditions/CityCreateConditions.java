package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static fr.openmc.core.features.city.commands.CityCommands.balanceCooldownTasks;

/**
 * Le but cette classe est de regrouper toutes les conditions neccessaires
 * pour creer une ville (utile pour faire une modif sur menu et commandes)
 */
public class CityCreateConditions {

    /**
     * Retourne un booleen pour dire si le joueur peut faire une ville
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityCreate(Player player) {
        if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
            MessagesManager.sendMessage(player, Component.text("§cTu dois attendre avant de pouvoir créer ta ville ("+ DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big")/1000 + " secondes)"), Prefix.CITY, MessageType.INFO, false);
            return false;
        }

        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERINCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (EconomyManager.getInstance().getBalance(player.getUniqueId()) < 3500) {
            MessagesManager.sendMessage(player, Component.text("§cTu n'a pas assez d'argent pour créer ta ville (3500 " + EconomyManager.getEconomyIcon() +")"), Prefix.CITY, MessageType.INFO, false);
            return false;
        }
        return true;
    }

}
