package fr.openmc.core.utils.text.messages;

import fr.openmc.core.features.settings.PlayerSettingsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

public class MessagesManager {

    /*
    For use the beautiful message, create a prefix.
     */

    private static final Map<Character, Character> SMALL_CHAR_MAP = Map.ofEntries(
            // Uppercase
            Map.entry('A', 'ᴀ'), Map.entry('B', 'ʙ'), Map.entry('C', 'ᴄ'), Map.entry('D', 'ᴅ'),
            Map.entry('E', 'ᴇ'), Map.entry('F', 'ꜰ'), Map.entry('G', 'ɢ'), Map.entry('H', 'ʜ'),
            Map.entry('I', 'ɪ'), Map.entry('J', 'ᴊ'), Map.entry('K', 'ᴋ'), Map.entry('L', 'ʟ'),
            Map.entry('M', 'ᴍ'), Map.entry('N', 'ɴ'), Map.entry('O', 'ᴏ'), Map.entry('P', 'ᴘ'),
            Map.entry('Q', 'ǫ'), Map.entry('R', 'ʀ'), Map.entry('S', 'ꜱ'), Map.entry('T', 'ᴛ'),
            Map.entry('U', 'ᴜ'), Map.entry('V', 'ᴠ'), Map.entry('W', 'ᴡ'), Map.entry('X', 'x'),
            Map.entry('Y', 'ʏ'), Map.entry('Z', 'ᴢ')
    );

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
                        .append(prefix.getPrefix())
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
        if (sender.isOnline() && sender instanceof Player player) {
            sendMessage(player, message, prefix, type, 1.0F, sound);
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
                        .append(prefix.getPrefix())
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
                        .append(prefix.getPrefix())
                        .append(Component.text(" §7» ")
                                .append(message)
                        );

        for (Player player : world.getPlayers()) {
            player.sendMessage(messageComponent);
        }
    }

    public static String textToSmall(String text) {
        if (text == null || text.isEmpty()) return text;

        // Pour retirer les accents
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        StringBuilder result = new StringBuilder(text.length());
        boolean colorCode = false;

        for (char c : text.toUpperCase().toCharArray()) {
            if (c == '§') {
                colorCode = true;
                result.append(c);
                continue;
            }

            if (colorCode) {
                result.append(c);
                colorCode = false;
                continue;
            }

            char upper = Character.toUpperCase(c);
            result.append(SMALL_CHAR_MAP.getOrDefault(upper, c));

        }

        return result.toString();
    }
}
