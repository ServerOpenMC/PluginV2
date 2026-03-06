package fr.openmc.core.commands.utils;

import fr.openmc.core.utils.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SetSpawn {
    
    @Command("setspawn")
    @Description("Permet de changer le spawn")
    @CommandPermission("omc.admin.commands.setspawn")
    public void setSpawn(Player player) {

        Location loc = player.getLocation();

        SpawnManager.setSpawn(loc);

        MessagesManager.sendMessage(player, TranslationManager.translation("command.utils.setspawn.success",
                Component.text(loc.getBlockX()).color(NamedTextColor.YELLOW),
                Component.text(loc.getBlockY()).color(NamedTextColor.YELLOW),
                Component.text(loc.getBlockZ()).color(NamedTextColor.YELLOW)
        ), Prefix.OPENMC, MessageType.SUCCESS, true);

    }
}
