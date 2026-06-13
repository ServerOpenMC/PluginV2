package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
    public static boolean canCityChangeType(City city, Player player, CityType toType) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.CHANGE_TYPE))) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.type.no_permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getType().equals(toType)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.type.already_in_type", toType.getDisplayName()), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!DynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
	        MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.type.must_wait",
                    Component.text(DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type")))
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getBalance() < REQUIRED_MONEY_TYPE) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.type.not_enough_city_money",
                    Component.text(REQUIRED_MONEY_TYPE + EconomyManager.getEconomyIcon())
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
