package fr.openmc.core.features.chat.animations;

import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.messages.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Random;

@Command("admchat")
@CommandPermission("omc.admins.commands.admchat")
public class AdmChatCommand {

    private static final Random RANDOM = new Random();

    @CommandPlaceholder()
    public void main(CommandSender sender) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            MessagesManager.sendMessage(sender, Component.text("§cSeuls les opérateurs peuvent utiliser cette commande."), Prefix.OPENMC, MessageType.ERROR, true);
            return;
        }

        // lancer aléatoirement un quiz ou un défi
        if (RANDOM.nextBoolean()) {
            ChatAnimations.startQuiz();
            MessagesManager.sendMessage(sender, Component.text("§aQuiz lancé."), Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            ChatAnimations.startChallenge();
            MessagesManager.sendMessage(sender, Component.text("§aDéfi lancé."), Prefix.OPENMC, MessageType.SUCCESS, true);
        }
    }
}

