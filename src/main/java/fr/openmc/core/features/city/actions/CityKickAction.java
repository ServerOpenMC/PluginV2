package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityKickCondition;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class CityKickAction {
    public static void startKick(Player sender, OfflinePlayer playerKick) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityKickCondition.canCityKickPlayer(city, sender, playerKick)) return;

        if (city == null) return;

        city.removePlayer(playerKick.getUniqueId());
        MessagesManager.sendMessage(sender, Component.text("Tu as exclu " + playerKick.getName() + " de la ville " + city.getName()), Prefix.CITY, MessageType.SUCCESS, false);

        if (playerKick.isOnline()) {
            MessagesManager.sendMessage((Player) playerKick, Component.text("Tu as été exclu de la ville " + city.getName()), Prefix.CITY, MessageType.INFO, true);
        }
    }
}
