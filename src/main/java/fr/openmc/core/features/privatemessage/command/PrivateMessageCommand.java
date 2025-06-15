package fr.openmc.core.features.privatemessage.command;

import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class PrivateMessageCommand {

    @Command("msg")
    @Description("Envoie un message privé à un joueur")
    @CommandPermission("omc.commands.privatemessage.msg")
    public void sendPrivateMessage(Player player, @Named("Receiver") Player target, @Named("message") String message) {
        PrivateMessageManager.getInstance().sendPrivateMessage(player, target, message);
    }

    @Command("r")
    @Description("Répond à un message privé du dernier joueur qui vous a envoyé un message")
    @CommandPermission("omc.commands.privatemessage.reply")
    public void replyToLastMessage(Player player, @Named("message") String message) {
        PrivateMessageManager.getInstance().replyToLastMessage(player, message);
    }

}
