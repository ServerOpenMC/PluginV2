package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import fr.openmc.core.features.city.CityChatManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public class CityChatCommand {
    @Command({"cc", "city chat", "ville chat"})
    @CommandPermission("omc.commands.city.chat")
    @Description("Activer ou désactiver le chat de ville")
    public void onCityChat(Player sender) {
        if (!CityChatManager.isCityChatMember(sender)) {
            CityChatManager.addCityChatMember(sender);
        } else {
            CityChatManager.removeCityChatMember(sender);
        }
    }
}
