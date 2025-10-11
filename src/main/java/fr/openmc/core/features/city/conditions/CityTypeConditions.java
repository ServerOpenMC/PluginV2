package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * touchant aux mascottes (utile pour faire une modif sur menu et commandes).
 */
public class CityTypeConditions {
    private static final int REQUIRED_MONEY_TYPE = 40000;

    /**
     * Retourne un booleen pour dire si la ville peut changer de typê
     *
     * @param city la ville sur laquelle on teste cela
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityChangeType(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.TYPE))) {
	        MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de changer le statut de ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
	        MessagesManager.sendMessage(player, Component.text("Vous devez attendre " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type")) + " seconde(s) pour changer de type de ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getBalance() < REQUIRED_MONEY_TYPE) {
            MessagesManager.sendMessage(player, Component.text("Vous devez avoir au moins " + REQUIRED_MONEY_TYPE + EconomyManager.getEconomyIcon() + " dans votre banque pour changer le type de votre ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
