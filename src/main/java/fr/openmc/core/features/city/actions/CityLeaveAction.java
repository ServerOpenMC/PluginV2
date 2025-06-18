package fr.openmc.core.features.city.actions;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class CityLeaveAction {

    public static void startLeave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) return;

        if (!CityLeaveCondition.canCityLeave(city, player)) return;

        city.removePlayer(player.getUniqueId());

        MessagesManager.sendMessage(player, Component.text("Tu as quitté " + city.getName()), Prefix.CITY, MessageType.SUCCESS, false);

        city.getOnlineMembers().forEach(memberUUID -> {
            if (memberUUID.equals(player.getUniqueId())) return;
            Player onlineMember = Bukkit.getPlayer(memberUUID);
            MessagesManager.sendMessage(onlineMember, Component.text(player.getName() + " a quitté la ville " + city.getName()), Prefix.CITY, MessageType.INFO, true);
        });
    }
}
