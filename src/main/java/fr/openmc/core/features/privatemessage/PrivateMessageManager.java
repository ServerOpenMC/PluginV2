package fr.openmc.core.features.privatemessage;

import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.features.privatemessage.command.PrivateMessageCommand;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Credit(developers = {"Axeno"})
public class PrivateMessageManager extends Feature implements HasCommands {

    private static final Map<UUID, UUID> lastMessageFrom = new HashMap<>();

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new PrivateMessageCommand()
        );
    }

    /**
     * Send a private message from sender to receiver.
     *
     * @param sender The player sending the message.
     * @param receiver The player receiving the message.
     * @param message The message to send.
     */
    public static void sendPrivateMessage(Player sender, Player receiver, String message) {
        if (sender.equals(receiver)) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.privatemessage.msg.cannot_message_yourself"), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        if (!PlayerSettingsManager.canReceivePrivateMessage(receiver.getUniqueId(), sender.getUniqueId())) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.privatemessage.msg.private_messages_disabled"), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }
        if (!PlayerSettingsManager.canReceivePrivateMessage(sender.getUniqueId(), receiver.getUniqueId())) {
            MessagesManager.sendMessage(sender, TranslationManager.translation("feature.privatemessage.msg.player_private_messages_disabled",
                    Component.text(receiver.getName()).color(NamedTextColor.YELLOW)), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        sender.sendMessage(TranslationManager.translation("feature.privatemessage.msg.format.sender",
                Component.text(receiver.getName()).color(NamedTextColor.BLUE),
                Component.text(message).color(NamedTextColor.WHITE)));
        receiver.sendMessage(TranslationManager.translation("feature.privatemessage.msg.format.receiver",
                Component.text(sender.getName()).color(NamedTextColor.YELLOW),
                Component.text(message).color(NamedTextColor.WHITE)));
        SocialSpyManager.broadcastToSocialSpy(sender, receiver, message);

        lastMessageFrom.put(receiver.getUniqueId(), sender.getUniqueId());
        lastMessageFrom.put(sender.getUniqueId(), receiver.getUniqueId());
    }

    /**
     * Reply to the last private message received by the sender.
     *
     * @param sender The player sending the message.
     * @param message The message to send.
     */
    public static void replyToLastMessage(Player sender, String message) {
         UUID lastReceiverId = lastMessageFrom.get(sender.getUniqueId());
         if (lastReceiverId == null) {
             MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.missing_arg"), Prefix.OPENMC, MessageType.ERROR, true);
             return;
         }

         Player receiver = sender.getServer().getPlayer(lastReceiverId);
         if (receiver == null || !receiver.isOnline()) {
             MessagesManager.sendMessage(sender, TranslationManager.translation("messages.global.player_not_found"), Prefix.OPENMC, MessageType.ERROR, true);
             return;
         }

        sendPrivateMessage(sender, receiver, message);
    }
}
