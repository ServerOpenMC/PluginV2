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
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public class CityChatCommand {
    @Command({"cc", "city chat", "ville chat"})
    @CommandPermission("omc.commands.city.chat")
    @Description("Envoyer un message dans le chat de votre ville")
    public void onCityChat(Player sender, @Named("message") String message) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
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
