package fr.openmc.core.features.privatemessage;

import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateMessageManager {

    private final Map<UUID, UUID> lastMessageFrom = new HashMap<>();
    @Getter private static PrivateMessageManager instance;
    @Getter private final SocialSpyManager spyManager;

    public PrivateMessageManager() {
        instance = this;
        this.spyManager = new SocialSpyManager();
    }

    /**
     * Send a private message from sender to receiver.
     *
     * @param sender The player sending the message.
     * @param receiver The player receiving the message.
     * @param message The message to send.
     */
    public void sendPrivateMessage(Player sender, Player receiver, String message) {
        if (sender.equals(receiver)) {
            MessagesManager.sendMessage(sender, Component.text("§cVous ne pouvez pas vous envoyer de message privé à " +
                    "vous-même."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }
        if (PlayerSettingsManager.getPlayerSettings(sender).canPerformAction(SettingType.PRIVATE_MESSAGE_POLICY, sender.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("§cVous avez désactivé les messages privés."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }
        if (PlayerSettingsManager.getPlayerSettings(receiver).canPerformAction(SettingType.PRIVATE_MESSAGE_POLICY, receiver.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("§cLe joueur " + receiver.getName() + " a désactivé les messages privés."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        sender.sendMessage("§7[§eToi §6§l→ §r§9" + receiver.getName() + "§7] §f" + message);
        receiver.sendMessage("§7[§e" + sender.getName() + " §6§l→ §r§9Toi§7] §f" + message);
        spyManager.broadcastToSocialSpy(sender, receiver, message);

        lastMessageFrom.put(receiver.getUniqueId(), sender.getUniqueId());
        lastMessageFrom.put(sender.getUniqueId(), receiver.getUniqueId());
    }

    /**
     * Reply to the last private message received by the sender.
     *
     * @param sender The player sending the message.
     * @param message The message to send.
     */
    public void replyToLastMessage(Player sender, String message) {
        UUID lastReceiverId = lastMessageFrom.get(sender.getUniqueId());
        if (lastReceiverId == null) {
            MessagesManager.sendMessage(sender, Component.text("§cVous n'avez pas de message privé récent."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        Player receiver = sender.getServer().getPlayer(lastReceiverId);
        if (receiver == null || !receiver.isOnline()) {
            MessagesManager.sendMessage(sender, Component.text("§cLe joueur n'est pas en ligne."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        sendPrivateMessage(sender, receiver, message);
    }
}
