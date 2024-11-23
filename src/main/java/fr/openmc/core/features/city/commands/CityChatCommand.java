package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import fr.openmc.core.features.city.CityManager;

import java.util.UUID;

public class CityChatCommand {
    @Command({"cc", "city chat", "ville chat"})
    public void onCityChat(Player sender, String message) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(sender, "Tu n'habites dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Component msg_component = Component.text("#ville ").color(NamedTextColor.GOLD).append(sender.displayName().color(NamedTextColor.WHITE)).append(
                Component.text(" » ").color(NamedTextColor.GRAY).append(
                        Component.text(message).color(NamedTextColor.WHITE)
                )
        );

        for (UUID uuid : city.getMembers()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player.isOnline()) {
                ((Player) player).sendMessage(msg_component);
            }
        }
    }
}
