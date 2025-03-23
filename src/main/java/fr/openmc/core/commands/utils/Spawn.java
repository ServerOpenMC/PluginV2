package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.messages.MessagesManager.Message;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Spawn {

    @Command("spawn")
    @Description("Permet de se rendre au spawn")
    @CommandPermission("omc.commands.spawn")
    public void spawn(CommandSender sender, @Default("me") Player target) {
        
        Location spawnLocation = SpawnManager.getInstance().getSpawnLocation();

        if(sender instanceof Player player && player == target) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(spawnLocation);
                            MessagesManager.sendMessage(player, Component.text("§aVous avez été envoyé au spawn"), Prefix.OPENMC, MessageType.SUCCESS, true);
                        }
                    }.runTaskLater(OMCPlugin.getInstance(), 10);
                }
            }.runTaskLater(OMCPlugin.getInstance(), 10);
        } else {
            if(!(sender instanceof Player) || ((Player) sender).hasPermission("omc.admin.commands.spawn.others")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.sendTitle(PlaceholderAPI.setPlaceholders(target, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                target.teleport(spawnLocation);
                                MessagesManager.sendMessage(sender, Component.text("§aVous avez envoyé §e" + target.getName() + "§a au spawn"), Prefix.OPENMC, MessageType.SUCCESS, true);
                                MessagesManager.sendMessage(target, Component.text("§aVous avez été envoyé au spawn par §e" + (sender instanceof Player player ? player.getName() : "Console") + "§a"), Prefix.OPENMC, MessageType.WARNING, true);                            }
                        }.runTaskLater(OMCPlugin.getInstance(), 10);
                    }
                }.runTaskLater(OMCPlugin.getInstance(), 10);
            } else {
                MessagesManager.sendMessage(sender, Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}
