package fr.openmc.core.features.city.sub.bank.conditions;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.CityType;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;


/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour tout ce qui est autour de la banque (utile pour faire une modif sur menu et commandes).
 */
public class CityBankConditions {

    /**
     * Retourne un booleen pour dire si le joueur peut ouvrir la banque
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canOpenCityBank(City city, OMCPlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CITY_BANK)) {
            player.message().sendError(TranslationManager.translation(
                    "feature.city.bank.errors.feature_locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CITY_BANK))
            ), Prefix.CITY, false);
            return false;
        }

        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut donner de l'argent à sa ville
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityDeposit(City city, OMCPlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!canOpenCityBank(city, player)) return false;

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_DEPOSIT))) {
            player.message().sendError(TranslationManager.translation("feature.city.bank.errors.no_permission_deposit"), Prefix.CITY, false);
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
    public static boolean canCityWithdraw(City city, OMCPlayer player) {
        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_WITHDRAW))) {
            player.message().sendError(TranslationManager.translation("feature.city.bank.errors.no_permission_withdraw"), Prefix.CITY, false);
            return false;
        }

        if (city.getType().equals(CityType.WAR)) {
            player.message().sendError(TranslationManager.translation("feature.city.bank.errors.war_blocked"), Prefix.CITY, false);
            return false;
        }

        return true;
    }
}
