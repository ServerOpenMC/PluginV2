package fr.openmc.core.features.privatemessage.command;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.privatemessage.PrivateMessageManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class PrivateMessageCommand {

    @Command({"msg", "w", "whisper", "tell"})
    @Description("Envoie un message privé à un joueur")
    @CommandPermission("omc.commands.privatemessage.msg")
    public void sendPrivateMessage(
            Player player,
            @Named("joueur") @SuggestWith(OnlinePlayerAutoComplete.class) Player target,
            @Named("message") String message
    ) {
        PrivateMessageManager.sendPrivateMessage(player, target, message);
    }

    @Command("r")
    @Description("Répond à un message privé du dernier joueur qui vous a envoyé un message")
    @CommandPermission("omc.commands.privatemessage.reply")
    public void replyToLastMessage(
            Player player,
            @Named("message") String message
    ) {
        PrivateMessageManager.replyToLastMessage(player, message);
    }

}
