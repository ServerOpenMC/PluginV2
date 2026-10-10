package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.CityType;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * touchant aux mascottes (utile pour faire une modif sur menu et commandes).
 */
public class CityTypeConditions {
    private static final EconomyManager economyManager = OMCRegistry.FEATURES.ECONOMY.get();
    private static final DynamicCooldownManager dynamicCooldownManager = OMCRegistry.FEATURES.DYNAMIC_COOLDOWN.get();
    private static final int REQUIRED_MONEY_TYPE = 40000;

    /**
     * Retourne un booleen pour dire si la ville peut changer de typê
     *
     * @param city la ville sur laquelle on teste cela
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityChangeType(City city, OMCPlayer player, CityType toType) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.CHANGE_TYPE))) {
	        player.message().sendError(TranslationManager.translation("feature.city.conditions.type.no_permission"), Prefix.CITY, false);
            return false;
        }

        if (city.getType().equals(toType)) {
            player.message().sendError(TranslationManager.translation("feature.city.conditions.type.already_in_type", toType.getDisplayName()), Prefix.CITY, false);
            return false;
        }

        if (!dynamicCooldownManager.isReady(city.getUniqueId(), "city:type")) {
	        player.message().sendError(TranslationManager.translation(
                    "feature.city.conditions.type.must_wait",
                    Component.text(DateUtils.convertMillisToTime(dynamicCooldownManager.getRemaining(city.getUniqueId(), "city:type")))
            ), Prefix.CITY, false);
            return false;
        }

        if (city.getBalance() < REQUIRED_MONEY_TYPE) {
            player.message().sendError(TranslationManager.translation(
                    "feature.city.conditions.type.not_enough_city_money",
                    Component.text(REQUIRED_MONEY_TYPE + economyManager.getEconomyIcon())
            ), Prefix.CITY, false);
            return false;
        }

        return true;
    }
}
