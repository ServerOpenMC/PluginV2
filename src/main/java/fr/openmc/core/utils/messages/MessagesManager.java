package fr.openmc.core.utils.messages;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;

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

        String messageStr = prefix.getPrefix() + " §7» " + message;

        if(sender instanceof Player player && sound) {
            player.playSound(player.getLocation(), getSound(type), 1, 1);
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


    private static String getPrefixType(MessageType type) {
        String prefixType;
        switch (type) {
            case ERROR:
                prefixType = "§c❗";
                break;
            case WARNING:
                prefixType = "§6⚠";
                break;
            case SUCCESS:
                prefixType = "§a✔";
                break;
            case INFO:
                prefixType = "§bⓘ";
                break;
            default:
                prefixType = "§7";
                break;
        }
        return prefixType;
    }

    private static Sound getSound(MessageType type) {
        Sound soundType;
        switch (type) {
            case ERROR:
                soundType = Sound.BLOCK_NOTE_BLOCK_BASS;
                break;
            case WARNING:
                soundType = Sound.BLOCK_NOTE_BLOCK_BASS;
                break;
            case SUCCESS:
                soundType = Sound.BLOCK_NOTE_BLOCK_BELL;
                break;
            case INFO:
                soundType = Sound.BLOCK_NOTE_BLOCK_BIT;
                break;
            default:
                soundType = null;
                break;
        }
        return soundType;
    }

    public static String textToSmall(String text) {
        StringBuilder result = new StringBuilder();
        String smallLetters = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡхʏᴢ";
        String normalLetters = "abcdefghijklmnopqrstuvwxyz";
        String normalLettersCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "₁₂₃₄₅₆₇₈₉₀";
        String numbersNormal = "1234567890";

        if (text.contains("§")) {
            String[] split = text.split("§");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    result.append(split[i]);
                    continue;
                }
                if (split[i].length() > 1) {
                    result.append("§" + split[i].charAt(0) + textToSmall(split[i].substring(1)));
                } else {
                    result.append("§" + split[i]);
                }
            }
            return result.toString();
        }

        for (char c : text.toCharArray()) {

            if (normalLetters.indexOf(c) != -1) {
                result.append(smallLetters.charAt(normalLetters.indexOf(c)));
            } else if (normalLettersCaps.indexOf(c) != -1) {
                result.append(smallLetters.charAt(normalLettersCaps.indexOf(c)));
            } else if (numbersNormal.indexOf(c) != -1) {
                result.append(numbers.charAt(numbersNormal.indexOf(c)));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static enum Message {
        NOPERMISSION("§cVous n'avez pas la permission d'exécuter cette commande."),

        ;

        @Getter private final String message;
        Message(String message) {
            this.message = message;
        }

    }

}