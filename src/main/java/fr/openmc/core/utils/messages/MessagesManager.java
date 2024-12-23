package fr.openmc.core.utils.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    public static void sendMessageType(CommandSender sender, Component message, Prefix prefix, MessageType type, boolean sound) {
        MiniMessage.miniMessage().deserialize("e");
        Component messageComponent =
                Component.text("§7(" + getPrefixType(type) + "§7) ")
                        .append(MiniMessage.miniMessage().deserialize(prefix.getPrefix()))
                        .append(Component.text(" §7» ")
                        .append(message)
                );

        if(sender instanceof Player player && sound) {
            player.playSound(player.getLocation(), getSound(type), 1, 1);
        }

        sender.sendMessage(messageComponent);

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
        Component messageComponent = MiniMessage.miniMessage().deserialize(prefix.getPrefix())
                .append(Component.text(" §7» "))
                .append(message);



        sender.sendMessage(messageComponent);

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
        NOPERMISSION(Component.text("§cVous n'avez pas la permission d'exécuter cette commande.")),
        MISSINGARGUMENT(Component.text("§cVous devez spécifier un argument.")),

        // City messages
        PLAYERNOCITY(Component.text("Tu n'es pas dans une ville")),
        PLAYERINCITY(Component.text("le joueur est déjà dans une ville")),

        ;

        private final Component message;
        Message(Component message) {
            this.message = message;
        }

    }

}
