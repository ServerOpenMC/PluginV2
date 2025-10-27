package fr.openmc.core.utils.messages;

import fr.openmc.core.features.settings.PlayerSettingsManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        
    public static String textToSmall(String text) {
        Map<Character, Character> charMap = Map.ofEntries(
                Map.entry('A', 'ᴀ'), Map.entry('B', 'ʙ'), Map.entry('C', 'ᴄ'), Map.entry('D', 'ᴅ'),
                Map.entry('E', 'ᴇ'), Map.entry('F', 'ꜰ'), Map.entry('G', 'ɢ'), Map.entry('H', 'ʜ'),
                Map.entry('I', 'ɪ'), Map.entry('J', 'ᴊ'), Map.entry('K', 'ᴋ'), Map.entry('L', 'ʟ'),
                Map.entry('M', 'ᴍ'), Map.entry('N', 'ɴ'), Map.entry('O', 'ᴏ'), Map.entry('P', 'ᴘ'),
                Map.entry('Q', 'ǫ'), Map.entry('R', 'ʀ'), Map.entry('S', 'ѕ'), Map.entry('T', 'ᴛ'),
                Map.entry('U', 'ᴜ'), Map.entry('V', 'ᴠ'), Map.entry('W', 'ᴡ'), Map.entry('X', 'ʏ'),
                Map.entry('Y', 'ʏ'), Map.entry('Z', 'ᴢ'), Map.entry('1', '₁'), Map.entry('2', '₂'),
                Map.entry('3', '₃'), Map.entry('4', '₄'), Map.entry('5', '₅'), Map.entry('6', '₆'),
                Map.entry('7', '₇'), Map.entry('8', '₈'), Map.entry('9', '₉'), Map.entry('0', '₀')
        );

        StringBuilder result = new StringBuilder();
        for(char c : text.toCharArray()) {
            result.append(charMap.getOrDefault(c, c));
        }

        return result.toString();
    }

    @Getter
    public enum Message {
        // Command messages
        NO_PERMISSION(Component.text("§cVous n'avez pas la permission d'exécuter cette commande.")),
        NO_PERMISSION_2(Component.text("§cVous n'avez pas le droit de faire ceci")),
        MISSING_ARGUMENT(Component.text("§cVous devez spécifier un argument.")),

        // Player messages
        PLAYER_NOT_FOUND(Component.text("§cLe joueur n'a pas été trouvé.")),

        // General messages
        PLAYER_MISSING_MONEY(Component.text("Tu n'as pas assez d'argent")),

        // City messages
        PLAYER_NO_CITY(Component.text("Tu n'es pas dans une ville")),
        PLAYER_IN_CITY(Component.text("Tu es déjà dans une ville")),
        CITY_NO_FREE_CLAIM(Component.text("Cette ville n'a pas de claims gratuits")),

        PLAYER_NO_ACCESS_PERMS(Component.text("Tu n'as pas la permission d'accéder aux permissions ou grades de cette ville")),
        PLAYER_NO_CLAIM(Component.text("Tu n'as pas la permission d'agrandir ta ville")),
        PLAYER_NO_OWNER(Component.text("Tu n'as pas la permission car tu n'es pas maire")),
        PLAYER_NO_RENAME(Component.text("Tu n'as pas la permission de renommer ta ville")),
        PLAYER_NO_MONEY_GIVE(Component.text("Tu n'as pas la permission de donner de l'argent à ta ville")),
        PLAYER_NO_MONEY_TAKE(Component.text("Tu n'as pas la permission de prendre de l'argent à ta ville")),
        PLAYER_IS_OWNER(Component.text("Le propriétaire a tous les pouvoirs.")),

        CITY_RANKS_NOT_EXIST(Component.text("Ce grade n'existe pas.")),
        CITY_RANKS_MAX(Component.text("Le nombre maximum de grades a été atteint, tu ne peux pas en ajouter d'autres.")),
        CITY_RANKS_ALREADY_EXIST(Component.text("Ce grade existe déjà.")),
        CITY_RANKS_CANNOT_DELETE(Component.text("Tu ne peux pas supprimer le grade de propriétaire.")),

        CITY_NOT_FOUND(Component.text("La ville n'existe pas")),

        ;

        private final Component message;

        Message(Component message) {
            this.message = message;
        }

    }

}
