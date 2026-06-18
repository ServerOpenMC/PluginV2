package fr.openmc.core.features.mailboxes.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"mailbox", "mb", "letter", "mail", "lettre", "boite", "courrier"})
@CommandPermission("omc.commands.mailbox")
public class MailboxCommand {

    @CommandPlaceholder()
    public void mailbox(Player player) {
        new PlayerMailbox(player).open();
    }
    
    @Subcommand("home")
    @Description("Ouvrir la page d'accueil de la boite aux lettres")
    public static void homeMailbox(Player player) {
        new HomeMailbox(player).open();
    }

    @Subcommand("send")
    @Description("Envoyer une lettre à un joueur")
    public void sendMailbox(Player player, @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) String receiver) {
        OfflinePlayer receiverPlayer = Bukkit.getPlayerExact(receiver);
        if (receiverPlayer == null) receiverPlayer = Bukkit.getOfflinePlayerIfCached(receiver);
        if (receiverPlayer == null || !(receiverPlayer.hasPlayedBefore() || receiverPlayer.isOnline())) {
            Component message = TranslationManager.translation(
                    "feature.mailboxes.message.player_not_found",
                    Component.text(receiver).color(NamedTextColor.RED)
            ).color(NamedTextColor.DARK_RED);
            MessagesManager.sendMessage(player, message, Prefix.MAILBOX, MessageType.ERROR, true);
            return;
        }
        if (receiverPlayer.getUniqueId() == player.getUniqueId()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.mailboxes.message.send_to_self")
                            .color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX, MessageType.ERROR, true);
            return;
        }
        if (!MailboxManager.canSend(player, receiverPlayer)) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.mailboxes.message.cannot_send",
                            Component.text(receiverPlayer.getName()).color(NamedTextColor.RED)
                    ).color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        new SendingLetter(player, receiverPlayer).open();
    }

    @Subcommand("pending")
    @Description("Ouvrir les lettres en attente de réception")
    public void pendingMailbox(Player player) {
        new PendingMailbox(player).open();
    }

    @SecretCommand
    @Subcommand("open")
    @Description("Ouvrir une lettre")
    public void openMailbox(Player player, @Named("id") @Range(min = 1, max = Integer.MAX_VALUE) int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null) return;
        LetterMenu mailbox = new LetterMenu(player, letter);
        mailbox.open();
    }

    @Subcommand("refuse")
    @SecretCommand
    @Description("Refuser une lettre")
    public void refuseMailbox(Player player, @Named("id") @Range(min = 1, max = Integer.MAX_VALUE) int id) {
        LetterMenu.refuseLetter(player, id);
    }

    @Subcommand("cancel")
    @SecretCommand
    @Description("Annuler une lettre")
    public void cancelMailbox(Player player, @Named("id") @Range(min = 1, max = Integer.MAX_VALUE) int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation(
                            "feature.mailboxes.message.letter_not_found",
                            Component.text(id).color(NamedTextColor.RED)
                    ).color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return;
        }
        MailboxMenuManager.sendConfirmMenuToCancelLetter(player, letter);
    }
}
