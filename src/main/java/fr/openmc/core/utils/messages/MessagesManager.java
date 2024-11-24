package fr.openmc.core.utils.messages;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;

public class MessagesManager {

    private final Prefix prefix;
    public MessagesManager(Prefix prefix) {
        this.prefix = prefix;
    }

    /*
    For use the beautiful message, create a prefix.
     */

    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     */
    public void error(CommandSender sender, String message) {
        sendMessageType(sender, message, MessageType.ERROR, true);
    }

    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     */
    public void warning(CommandSender sender, String message) {
        sendMessageType(sender, message, MessageType.WARNING, true);
    }

    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     */
    public void success(CommandSender sender, String message) {
        sendMessageType(sender, message, MessageType.SUCCESS, true);
    }

    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     */
    public void info(CommandSender sender, String message) {
        sendMessageType(sender, message, MessageType.INFO, true);
    }

    private void sendMessageType(CommandSender sender, String message, MessageType type, boolean sound) {

        String messageStr = "§7(" + getPrefixType(type) + "§7) " + this.prefix.getPrefix() + " §7» " + message;

        if(sender instanceof Player player && sound) {
            player.playSound(player.getLocation(), getSound(type), 1, 1);
        }

        sender.sendMessage(messageStr);

    }


    /**
     * Sends a formatted message to the player with or without sound.
     *
     * Deprecated
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     * @param prefix  The prefix for the message
     * @param type    The type of message (information, error, success, warning)
     * @param sound   Indicates whether a sound should be played (true) or not (false)
     */
    @Deprecated
    public static void sendMessageType(CommandSender sender, String message, Prefix prefix, MessageType type, boolean sound) {

        String messageStr = "§7(" + getPrefixType(type) + "§7) " + prefix.getPrefix() + " §7» " + message;

        if(sender instanceof Player player && sound) {
            player.playSound(player.getLocation(), getSound(type), 1, 1);
        }

        sender.sendMessage(messageStr);

    }


    /**
     * Sends a formatted message to the player with an accompanying sound.
     *
     * Deprecated
     *
     * @param sender  The player to send the message to (can be a console)
     * @param message The content of the message
     * @param prefix  The prefix for the message
     */
    @Deprecated
    public static void sendMessage(CommandSender sender, String message, Prefix prefix) {
        String messageStr = prefix.getPrefix() + " §7» " + message;

        sender.sendMessage(messageStr);

    }


    private static String getPrefixType(MessageType type) {
        return switch (type) {
            case ERROR -> "§c❗";
            case WARNING -> "§6⚠";
            case SUCCESS -> "§a✔";
            case INFO -> "§bⓘ";
            default -> "§7";
        };
    }

    private static Sound getSound(MessageType type) {
        return switch (type) {
            case ERROR, WARNING -> Sound.BLOCK_NOTE_BLOCK_BASS;
            case SUCCESS -> Sound.BLOCK_NOTE_BLOCK_BELL;
            case INFO -> Sound.BLOCK_NOTE_BLOCK_BIT;
            default -> null;
        };
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
                    result.append("§").append(split[i].charAt(0)).append(textToSmall(split[i].substring(1)));
                } else {
                    result.append("§").append(split[i]);
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