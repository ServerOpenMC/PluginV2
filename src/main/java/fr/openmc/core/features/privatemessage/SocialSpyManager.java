package fr.openmc.core.features.privatemessage;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.features.privatemessage.command.SocialSpyCommand;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Credit(developers = {"Axeno"})
public class SocialSpyManager extends Feature implements HasCommands  {
    private static final Set<UUID> socialSpyEnabled = new HashSet<>();

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new SocialSpyCommand()
        );
    }

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
                     TranslationManager.translation("feature.privatemessage.socialspy.toggled_off"),
                     Prefix.OPENMC, MessageType.SUCCESS, true);
         } else {
             socialSpyEnabled.add(playerUUID);
             MessagesManager.sendMessage(player,
                     TranslationManager.translation("feature.privatemessage.socialspy.toggled_on"),
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
        Component socialSpyMessage = TranslationManager.translation(
                "feature.privatemessage.socialspy.format",
                Component.text(sender.getName()).color(NamedTextColor.GRAY),
                Component.text(receiver.getName()).color(NamedTextColor.GRAY),
                Component.text(message).color(NamedTextColor.GRAY)
        );

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
