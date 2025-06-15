package fr.openmc.core.features.privatemessage;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Named;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateMessageManager {

    private final Map<UUID, UUID> lastMessageFrom = new HashMap<>();
    @Getter private static PrivateMessageManager instance;

    public PrivateMessageManager() {
        instance = this;
    }

    public void sendPrivateMessage(Player sender, Player receiver, String message) {
        if (sender.equals(receiver)) {
            MessagesManager.sendMessage(sender, Component.text("§cVous ne pouvez pas vous envoyer de message privé à " +
                    "vous-même."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        sender.sendMessage("§7[§eToi §6§l→ §r§9" + receiver.getName() + "§7] §f" + message);
        receiver.sendMessage("§7[§a" + sender.getName() + " §7→ §9Toi§7] §f" + message);
        receiver.sendMessage("§7[§e" + sender.getName() + " §6§l→ §r§9Toi§7] §f" + message);

        lastMessageFrom.put(receiver.getUniqueId(), sender.getUniqueId());
        lastMessageFrom.put(sender.getUniqueId(), receiver.getUniqueId());
    }

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
