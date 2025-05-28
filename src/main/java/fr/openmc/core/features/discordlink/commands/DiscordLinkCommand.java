package fr.openmc.core.features.discordlink.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.discordlink.DiscordLinkManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

@Command({"discordlink", "link"})
public class DiscordLinkCommand {

    @DefaultFor("~")
    void mainCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessagesManager.sendMessage(sender, Component.text("Cette commande est réservée aux joueurs."),
                    Prefix.DISCORD, MessageType.ERROR, false);
            return;
        }

        Player player = (Player) sender;
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean isLinked = DiscordLinkManager.getInstance().isLinked(player.getUniqueId()).join();
                if (isLinked) {
                    MessagesManager.sendMessage(player,
                            Component.text("Ton compte est déjà lié à Discord."),
                            Prefix.DISCORD, MessageType.ERROR, false);
                    return;
                }

                String code = DiscordLinkManager.getInstance().generateVerificationCode(player);

                Component message = Component.text()
                        .append(Component.text("Voici ton code de vérification: ", NamedTextColor.GRAY))
                        .append(Component.text(code, NamedTextColor.YELLOW))
                        .append(Component.newline())
                        .append(Component.text("Utilise la commande ", NamedTextColor.GRAY))
                        .append(Component.text("/link " + code, NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" sur Discord.", NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Ce code expirera dans ", NamedTextColor.GRAY))
                        .append(Component.text("5 minutes", NamedTextColor.RED))
                        .append(Component.text(".", NamedTextColor.GRAY))
                        .build();

                MessagesManager.sendMessage(player, message, Prefix.DISCORD, MessageType.SUCCESS, false);
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    @Subcommand("status")
    void checkLinkStatus(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String discordId = DiscordLinkManager.getInstance().getDiscordId(player.getUniqueId()).join();

                if (discordId != null) {
                    MessagesManager.sendMessage(player,
                            Component.text("Ton compte Minecraft est lié à Discord (ID: ")
                                    .append(Component.text(discordId, NamedTextColor.GREEN)),
                            Prefix.DISCORD, MessageType.SUCCESS, false);
                } else {
                    MessagesManager.sendMessage(player,
                            Component.text("Ton compte Minecraft n'est pas lié à Discord. Utilise ")
                                    .append(Component.text("/link", NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text(" pour commencer.", NamedTextColor.GRAY)),
                            Prefix.DISCORD, MessageType.INFO, false);
                }
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    @Subcommand("unlink")
    void unlinkAccount(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean success = DiscordLinkManager.getInstance().unlinkAccount(player.getUniqueId()).join();

                if (success) {
                    MessagesManager.sendMessage(player,
                            Component.text("Ton compte a été dissocié avec succès."),
                            Prefix.DISCORD, MessageType.SUCCESS, false);
                } else {
                    MessagesManager.sendMessage(player,
                            Component.text("Ton compte n'est pas lié à Discord."),
                            Prefix.DISCORD, MessageType.ERROR, false);
                }
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }

    // Commande admin pour vérifier les liaisons
    @Subcommand("check")
    @CommandPermission("omc.admin.commands.discordlink.check")
    void adminCheckCommand(CommandSender sender, Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String discordId = DiscordLinkManager.getInstance().getDiscordId(target.getUniqueId()).join();

                if (discordId != null) {
                    MessagesManager.sendMessage(sender,
                            Component.text("Le joueur ")
                                    .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                                    .append(Component.text(" est lié à Discord (ID: "))
                                    .append(Component.text(discordId, NamedTextColor.GREEN)),
                            Prefix.DISCORD, MessageType.SUCCESS, false);
                } else {
                    MessagesManager.sendMessage(sender,
                            Component.text("Le joueur ")
                                    .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                                    .append(Component.text(" n'est pas lié à Discord.")),
                            Prefix.DISCORD, MessageType.INFO, false);
                }
            }
        }.runTaskAsynchronously(OMCPlugin.getInstance());
    }
}