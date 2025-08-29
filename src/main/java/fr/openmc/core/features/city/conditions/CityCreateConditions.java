package fr.openmc.core.features.city.conditions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
            MessagesManager.sendMessage(player, Component.text("§cTu dois attendre avant de pouvoir créer ta ville ("+ DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big")/1000 + " secondes)"), Prefix.CITY, MessageType.INFO, false);
            return false;
        }

        if (CityManager.getPlayerCity(player.getUniqueId()) != null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_IN_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (EconomyManager.getBalance(player.getUniqueId()) < MONEY_CREATE) {
            MessagesManager.sendMessage(player, Component.text("§cTu n'as pas assez d'Argent pour créer ta ville (" + MONEY_CREATE).append(Component.text(EconomyManager.getEconomyIcon() +" §cnécessaires)")).decoration(TextDecoration.ITALIC, false), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest(), AYWENITE_CREATE)) {
            MessagesManager.sendMessage(player, Component.text("§cTu n'as pas assez d'§dAywenite §cpour créer ta ville (" + AYWENITE_CREATE +" nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (cityName != null && !InputUtils.isInputCityName(cityName)) {
            MessagesManager.sendMessage(player, Component.text("Le nom de ville est invalide, il doit contenir seulement des caractères alphanumerique et doit faire moins de 24 charactères"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

}
