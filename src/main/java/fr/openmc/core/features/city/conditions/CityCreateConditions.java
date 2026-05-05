package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.InputUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour creer une ville (utile pour faire une modif sur menu et commandes).
 */
public class CityCreateConditions {

    public static final double MONEY_CREATE = 3500.0;
    public static final int AYWENITE_CREATE = 30;

    /**
     * Retourne un booleen pour dire si le joueur peut faire une ville
     *
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityCreate(Player player, String cityName) {
        if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.create.must_wait",
                    Component.text(DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big") / 1000)
            ), Prefix.CITY, MessageType.INFO, false);
            return false;
        }

        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_already_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (EconomyManager.getBalance(player.getUniqueId()) < MONEY_CREATE) {
	        MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.create.not_enough_player_money",
                    Component.text(MONEY_CREATE + EconomyManager.getEconomyIcon())
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest(), AYWENITE_CREATE)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.resource.not_enough_aywenite", Component.text(AYWENITE_CREATE)), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (cityName != null && !InputUtils.isInputCityName(cityName)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.commands.rename.invalid_name", Component.text(InputUtils.MAX_LENGTH_CITY)), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

}
