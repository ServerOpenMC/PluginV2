package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.CHEST)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir le coffre"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getChestWatcher() != null) {
            MessagesManager.sendMessage(player, Component.text("Le coffre est déjà ouvert par par §c" + Bukkit.getPlayer(city.getChestWatcher()).getName()), Prefix.CITY, MessageType.ERROR, false);
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
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas dans une ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.CHEST_UPGRADE)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions d'améliorer le coffre de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getChestPages() >= 5) {
            MessagesManager.sendMessage(player, Component.text("Le coffre de la Ville est déjà au niveau maximum"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        int price = city.getChestPages() * UPGRADE_PER_MONEY; // fonction linéaire f(x)=ax ; a=UPGRADE_PER_MONEY
        if (city.getBalance() < price) {
            MessagesManager.sendMessage(player, Component.text("La ville n'as pas assez d'argent (" + price + EconomyManager.getEconomyIcon() + " nécessaires)"), Prefix.CITY, MessageType.ERROR, true);
            return false;
        }

        int aywenite = city.getChestPages() * UPGRADE_PER_AYWENITE; // fonction linéaire f(x)=ax ; a=UPGRADE_PER_MONEY
        if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest(), aywenite)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'§dAywenite §f(" + aywenite + " nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
