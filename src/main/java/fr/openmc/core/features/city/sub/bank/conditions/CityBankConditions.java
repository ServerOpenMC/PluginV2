package fr.openmc.core.features.city.sub.bank.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;


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
    public static boolean canOpenCityBank(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CITY_BANK)) {
	        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CITY_BANK) + " !"), Prefix.CITY, MessageType.ERROR, false);
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
    public static boolean canCityDeposit(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!canOpenCityBank(city, player)) return false;

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_GIVE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de donner de l'argent à ta ville"), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_BALANCE))) {
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
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.MONEY_TAKE))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de prendre de l'argent de ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getType().equals(CityType.WAR)) {
	        MessagesManager.sendMessage(player, Component.text("Votre ville est en situation de guerre, vous ne pouvez faire cela"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
