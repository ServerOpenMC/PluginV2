package fr.openmc.core.utils.messages;

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
        Map<Character, Character> charMap = new HashMap<>();

        String smallLetters = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡхʏᴢ";
        String normalLetters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "₁₂₃₄₅₆₇₈₉₀";
        String numbersNormal = "1234567890";

        for (int i = 0; i < 26; i++) {
            charMap.put(normalLetters.charAt(i), smallLetters.charAt(i));
            charMap.put(normalLetters.charAt(i + 26), smallLetters.charAt(i));
        }
        for (int i = 0; i < numbersNormal.length(); i++) {
            charMap.put(numbersNormal.charAt(i), numbers.charAt(i));
        }


        for (char c : text.toCharArray()) {
            if (charMap.containsKey(c)) {
                result.append(charMap.get(c));
            } else {
                result.append(c);
            }
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