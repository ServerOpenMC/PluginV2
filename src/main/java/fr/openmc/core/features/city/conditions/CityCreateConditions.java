package fr.openmc.core.features.city.conditions;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour creer une ville (utile pour faire une modif sur menu et commandes).
 */
public class CityCreateConditions {
    private static final EconomyManager ECONOMY_MANAGER = OMCRegistry.FEATURES.ECONOMY.get();

    public static final double MONEY_CREATE = 3500.0;
    public static final int AYWENITE_CREATE = 30;

    /**
     * Retourne un booleen pour dire si le joueur peut faire une ville
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityCreate(OMCPlayer player, String cityName) {
        if (!player.cooldown().isReady("city:big")) {
            player.message().sendInfo(TranslationManager.translation(
                    "feature.city.conditions.create.must_wait",
                    Component.text(player.cooldown().getRemaining("city:big") / 1000)
            ), Prefix.CITY, true);
            return false;
        }

        if (City.ofPlayer(player.getUniqueId()) != null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_already_in_city"),
                    Prefix.CITY, true);
            return false;
        }

        if (player.economy().getBalance() < MONEY_CREATE) {
            player.message().sendError(TranslationManager.translation(
                    "feature.city.conditions.create.not_enough_player_money",
                    Component.text(MONEY_CREATE + ECONOMY_MANAGER.getEconomyIcon())
            ), Prefix.CITY, true);
            return false;
        }

        if (!ItemUtils.hasEnoughItems(player, OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest(), AYWENITE_CREATE)) {
            player.message().sendError(TranslationManager.translation("core.utils.aywenite.not_enough",
                            Component.text(AYWENITE_CREATE),
                            OMCRegistry.CUSTOM_ITEMS.AYWENITE.getSprite()
                    ), Prefix.CITY, true);
            return false;
        }

        if (cityName != null && !InputUtils.isInputCityName(cityName)) {
            player.message().sendError(TranslationManager.translation("feature.city.commands.rename.invalid_name",
                    Component.text(InputUtils.MAX_LENGTH_CITY)), Prefix.CITY, true);
            return false;
        }

        return true;
    }

}
