package fr.openmc.core.commands.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.messages.MessagesManager.Message;
import revxrsal.commands.annotation.*;

public class Spawn {

    @Command("spawn")
    @Description("Permet de se rendre au spawn")
    public void spawn(Player player, @Default("me") Player target) {
        
        Location spawnLocation = OMCPlugin.getInstance().getSpawnManager().getSpawnLocation();

        if(target.equals(player)) {
            player.teleport(spawnLocation);
            MessagesManager.sendMessageType(player, "§aVous avez été envoyé au spawn", Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            if(player.hasPermission("omc.command.spawn.others")) {
                target.teleport(spawnLocation);
                MessagesManager.sendMessageType(player, "§aVous avez envoyer §e" + target.getName() + "§a au spawn", Prefix.OPENMC, MessageType.SUCCESS, true);
                MessagesManager.sendMessageType(target, "§aVous avez été envoyé par §e" + player.getName() + "§a au spawn", Prefix.OPENMC, MessageType.WARNING, true);
            } else {
                MessagesManager.sendMessageType(player, Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}