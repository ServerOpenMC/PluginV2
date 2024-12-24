package fr.openmc.core.utils.messages;

import com.google.common.collect.ImmutableBiMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
    public static void sendMessageType(CommandSender sender, String message, Prefix prefix, MessageType type, boolean sound) {

        String messageStr = "§7(" + type.getPrefix() + "§7) " + prefix.getPrefix() + " §7» " + message;

        if(sender instanceof Player player && sound) {
            player.playSound(player.getLocation(), type.getSound(), 1, 1);
        }

        sender.sendMessage(messageStr);

    }


    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     * @param prefix  The prefix for the message
     */
    public static void sendMessage(CommandSender sender, String message, Prefix prefix) {
        String messageStr = prefix.getPrefix() + " §7» " + message;

        sender.sendMessage(messageStr);
    }

    public static String textToSmall(String text) {
        StringBuilder result = new StringBuilder();
        Map<Character, Character> charMap = ImmutableBiMap.<Character, Character>builder()
                .put('A', 'ᴀ').put('B', 'ʙ').put('C', 'ᴄ').put('D', 'ᴅ').put('E', 'ᴇ')
                .put('F', 'ꜰ').put('G', 'ɢ').put('H', 'ʜ').put('I', 'ɪ').put('J', 'ᴊ')
                .put('K', 'ᴋ').put('L', 'ʟ').put('M', 'ᴍ').put('N', 'ɴ').put('O', 'ᴏ')
                .put('P', 'ǫ').put('Q', 'ʀ').put('R', 'ʀ').put('S', 'ѕ').put('T', 'ᴛ')
                .put('U', 'ᴜ').put('V', 'ᴠ').put('W', 'ᴡ').put('X', 'ʏ').put('Y', 'ʏ').put('Z', 'ᴢ')
                .put('1', '₁').put('2', '₂').put('3', '₃').put('4', '₄').put('5', '₅')
                .put('6', '₆').put('7', '₇').put('8', '₈').put('9', '₉').put('0', '₀')
                .build();

        for (char c : text.toCharArray()) {
            result.append(charMap.getOrDefault(c, c));
        }

        return result.toString();
    }

    @Getter
    public enum Message {
        NOPERMISSION("§cVous n'avez pas la permission d'exécuter cette commande."),
        MISSINGARGUMENT("§cVous devez spécifier un argument."),

        // City messages
        PLAYERNOCITY("Tu n'es pas dans une ville"),
        PLAYERINCITY("le joueur est déjà dans une ville"),

        ;

        private final String message;
        Message(String message) {
            this.message = message;
        }

    }

}