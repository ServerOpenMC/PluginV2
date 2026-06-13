package fr.openmc.core.features.mailboxes.utils;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class MailboxMenuManager {
    public static void sendConfirmMenuToCancelLetter(Player player, Letter letter) {
        new ConfirmMenu(player,
                () -> {
                    PendingMailbox.cancelLetter(player, letter.getLetterId());
                    new PendingMailbox(player).open();
                    MessagesManager.sendMessage(
                            player,
                            Component.text("Vous avez annulé la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN),
                            Prefix.MAILBOX,
                            MessageType.SUCCESS,
                            false
                    );
                },
                player::closeInventory,
                List.of(Component.text("Confirmer l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.RED)),
                List.of(Component.text("Annuler l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN))
        ).open();
    }
}