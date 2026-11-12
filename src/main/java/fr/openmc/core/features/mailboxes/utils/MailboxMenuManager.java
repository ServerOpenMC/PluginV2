package fr.openmc.core.features.mailboxes.utils;

import fr.openmc.api.menulib.template.ConfirmMenuTemplate;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class MailboxMenuManager {
    public static void sendConfirmMenuToCancelLetter(Player player, Letter letter) {
        new ConfirmMenuTemplate(player,
                () -> {
                    PendingMailbox.cancelLetter(player, letter.getLetterId());
                    new PendingMailbox(player).open();
                    MessagesManager.sendMessage(
                            player,
                            TranslationManager.translation(
                                    "feature.mailboxes.menu.cancel.success",
                                    Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                            ).color(NamedTextColor.GREEN),
                            Prefix.MAILBOX,
                            MessageType.SUCCESS,
                            false
                    );
                },
                player::closeInventory,
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.confirm",
                        Component.text(letter.getLetterId()).color(NamedTextColor.RED)
                ).color(NamedTextColor.RED)),
                List.of(TranslationManager.translation(
                        "feature.mailboxes.menu.cancel.cancel",
                        Component.text(letter.getLetterId()).color(NamedTextColor.GREEN)
                ).color(NamedTextColor.GREEN))
        ).open();
    }
}