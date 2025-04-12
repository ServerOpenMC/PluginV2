package fr.openmc.core.features.quests.command;

import fr.openmc.core.features.quests.menus.QuestsMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;

@Command({"quest"})
@Description("Commande pour les quêtes")
public class QuestCommand {

    @DefaultFor({"~"})
    @Description("Ouvre le menu des quêtes")
    public void onQuest(Player player) {
        new QuestsMenu(player).open();
    }

    @Subcommand("open")
    @Description("Ouvre le menu des quêtes")
    public void resetProgress(Player sender, @Optional Player target) {
        if (target == null || target == sender) {
            new QuestsMenu(sender).open();
        } else {
            if (sender.hasPermission("omc.quests.admin")) new QuestsMenu(target).open();
            else MessagesManager.sendMessage(sender, MessagesManager.Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);

        }
    }
}
