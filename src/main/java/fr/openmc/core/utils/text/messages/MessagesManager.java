package fr.openmc.core.utils.text.messages;

import fr.openmc.core.features.settings.PlayerSettingsManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MessagesManager {

    /*
    For use the beautiful message, create a prefix.
     */

    /**
     * Sends a formatted message to the player with or without sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     * @param prefix  The prefix for the message
     * @param type    The type of message (information, error, success, warning)
     * @param sound   Indicates whether a sound should be played (true) or not (false)
     */
    public static void sendMessage(CommandSender sender, Component message, Prefix prefix, MessageType type, float soundVolume, boolean sound) {
        Component messageComponent =
                Component.text(type == MessageType.NONE ? "" : "§7(" + type.getPrefix() + "§7) ")
                        .append(prefix.getPrefix().font(Key.key("omc_fonts", "small_caps")))
                        .append(Component.text(" §7» ")
                        .append(message)
                );

        if(sender instanceof Player player && sound && PlayerSettingsManager.shouldPlayNotificationSound(player.getUniqueId())) {
            player.playSound(player.getLocation(), type.getSound(), soundVolume, 1.0F);
        }

        sender.sendMessage(messageComponent);
    }

    public static void sendMessage(CommandSender sender, Component message, Prefix prefix, MessageType type, boolean sound) {
        sendMessage(sender, message, prefix, type, 1.0F, sound);
    }

    public static void sendMessage(Player sender, Component message, Prefix prefix, MessageType type, boolean sound) {
        sendMessage(sender, message, prefix, type, 1.0F, sound);
    }

    public static void sendMessage(OfflinePlayer sender, Component message, Prefix prefix, MessageType type, boolean sound) {
        if (sender.isOnline()) {
            sendMessage(Bukkit.getPlayer(sender.getUniqueId()), message, prefix, type, 1.0F, sound);
        }
    }

    /**
     *
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     * @param prefix  The prefix for the message
     */
    public static void sendMessage(CommandSender sender, Component message, Prefix prefix) {
        sendMessage(sender, message, prefix, MessageType.NONE, false);
    }

    /**
     * Sends a message to the player.
     * @param player The player to send the message
     * @param messages The list of component which will be concatenated and sent to the player
     */
    public static void sendMessage(Player player, List<Component> messages) {
        Component messageComponent = Component.empty();

        for (Component component : messages) {
            messageComponent = messageComponent.appendNewline().append(component);
        }

        player.sendMessage(messageComponent);
    }

    /**
     *
     * Broadcasts a formatted message to the entire server
     *
     * @param message The content of the message
     * @param prefix  The prefix for the message
     * @param type    The type of message (information, error, success, warning)
     */
    public static void broadcastMessage(Component message, Prefix prefix, MessageType type) {
        Component messageComponent =
                Component.text(type == MessageType.NONE ? "" : "§7(" + type.getPrefix() + "§7) ")
                        .append(prefix.getPrefix().font(Key.key("omc_fonts", "small_caps")))
                        .append(Component.text(" §7» ")
                        .append(message)
                );

        Bukkit.broadcast(messageComponent);
    }

    /**
     *
     * Broadcasts a formatted message to the entire server
     *
     * @param world   The world to broadcast the message in
     * @param message The content of the message
     * @param prefix  The prefix for the message
     * @param type    The type of message (information, error, success, warning)
     */
    public static void broadcastMessage(World world, Component message, Prefix prefix, MessageType type) {
        Component messageComponent =
                Component.text(type == MessageType.NONE ? "" : "§7(" + type.getPrefix() + "§7) ")
                        .append(prefix.getPrefix().font(Key.key("omc_fonts", "small_caps")))
                        .append(Component.text(" §7» ")
                                .append(message)
                        );

        for (Player player : world.getPlayers()) {
            player.sendMessage(messageComponent);
        }
    }
}
