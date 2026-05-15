package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.milestone.rewards.ChestPageLimitRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour tout ce qui est autour du coffre de ville (utile pour faire une modif sur menu et commandes).
 */
public class CityChestConditions {

    public static final int UPGRADE_PER_MONEY = 5000;
    public static final int UPGRADE_PER_AYWENITE = 10;

    /**
     * Retourne un booleen pour dire si le joueur peut donner de l'argent à sa ville
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityChestOpen(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.CHEST)) {
	        MessagesManager.sendMessage(player, TranslationManager.translation(
                    "messages.city.havent_unlocked_feature",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.CHEST))
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.ACCESS_CITY_CHEST)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.chest.open.no_permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getChestWatcher() != null) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.chest.open.already_opened_by",
                    Component.text(Bukkit.getPlayer(city.getChestWatcher()).getName())
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }
        return true;
    }

    /**
     * Retourne un booleen pour dire si le joueur peut améliorer le coffre de sa ville
     *
     * @param city   la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityChestUpgrade(City city, Player player) {
        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CityPermission.UPGRADE_CHEST)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.chest.upgrade.no_permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getChestPages() >= ChestPageLimitRewards.getChestPageLimit(city.getLevel())) {
	        MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.conditions.chest.upgrade.max_level",
                    Component.text(ChestPageLimitRewards.getChestPageLimit(city.getLevel()))
            ), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        int price = city.getChestPages() * UPGRADE_PER_MONEY; // fonction linéaire f(x)=ax ; a=UPGRADE_PER_MONEY
        if (city.getBalance() < price) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "messages.city.city_not_enough_money",
                    Component.text(price + EconomyManager.getEconomyIcon())
            ), Prefix.CITY, MessageType.ERROR, true);
            return false;
        }

        int aywenite = city.getChestPages() * UPGRADE_PER_AYWENITE; // fonction linéaire f(x)=ax ; a=UPGRADE_PER_MONEY
        if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest(), aywenite)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.conditions.resource.not_enough_aywenite", Component.text(aywenite)), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
