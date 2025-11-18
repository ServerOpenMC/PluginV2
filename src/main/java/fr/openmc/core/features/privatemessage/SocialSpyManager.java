package fr.openmc.core.features.privatemessage;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialSpyManager {
    private static final Set<UUID> socialSpyEnabled = new HashSet<>();

    /**
     * Toggles the social spy feature for the player.
     *
     * @param player The player whose social spy status is being toggled.
     */
    public static void toggleSocialSpy(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (socialSpyEnabled.contains(playerUUID)) {
            socialSpyEnabled.remove(playerUUID);
            MessagesManager.sendMessage(player,
                    Component.text("§aSocial Spy désactivé."),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            socialSpyEnabled.add(playerUUID);
            MessagesManager.sendMessage(player,
                    Component.text("§aSocial Spy activé."),
                    Prefix.OPENMC, MessageType.SUCCESS, true);
        }
    }

    /**
     * Checks if the social spy feature is enabled for the player.
     *
     * @param player The player to check.
     * @return true if social spy is enabled, false otherwise.
     */
    public static boolean hasSocialSpyEnabled(Player player) {
        return socialSpyEnabled.contains(player.getUniqueId());
    }

    /**
     * Broadcasts a private message to all players with social spy enabled.
     *
     * @param sender The player sending the message.
     * @param receiver The player receiving the message.
     * @param message The message being sent.
     */
    public static void broadcastToSocialSpy(Player sender, Player receiver, String message) {
        String socialSpyMessage =
                "§8[§6SPY§8] §7" + sender.getName() + " §6→ §7" + receiver.getName() + "§8: §7" + message;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(sender) || onlinePlayer.equals(receiver)) {
                continue;
            }

            if (hasSocialSpyEnabled(onlinePlayer) && onlinePlayer.hasPermission("omc.commands.privatemessage.socialspy")) {
                onlinePlayer.sendMessage(socialSpyMessage);
            }
        }
    }
}
