package fr.openmc.core.features.city.conditions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityClaimAction;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Le but de cette classe est de regrouper toutes les conditions necessaires
 * pour claim une zone (utile pour faire une modif sur menu et commandes).
 */
public class CityClaimCondition {

    /**
     * Retourne un booleen pour dire si la ville peut etre etendu
     *
     * @param city la ville sur laquelle on fait les actions
     * @param player le joueur sur lequel tester les permissions
     * @return booleen
     */
    public static boolean canCityClaim(City city, Player player) {
        if (player.getWorld() != Bukkit.getWorld("world")) return false;
        
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(player.getUniqueId(), CityPermission.CLAIM))) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as pas la permission de claim"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.getFreeClaims() > 0) return true;

        int amount = CityClaimAction.calculateAywenite(city.getChunks().size());
        if (!ItemUtils.hasEnoughItems(player, CustomItemRegistry.getByName("omc_items:aywenite").getBest(), amount)) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas assez d'§dAywenite §f(" + amount + " nécessaires)"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    false
            );
            return false;
        }

        double money = CityClaimAction.calculatePrice(city.getChunks().size());

        if (city.getBalance() < money) {
            MessagesManager.sendMessage(player, Component.text("§cTu n'as pas assez d'argent dans ta banque pour claim (" + money).append(Component.text(EconomyManager.getEconomyIcon() + " §cnécessaires)")).decoration(TextDecoration.ITALIC, false), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
}
