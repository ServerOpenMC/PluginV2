package fr.openmc.core.features.mailboxes;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
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
            Component message = Component.text("Le joueur ", NamedTextColor.DARK_RED)
                                         .append(Component.text(receiver, NamedTextColor.RED))
                                         .append(Component.text(" n'existe pas ou ne s'est jamais connecté !", NamedTextColor.DARK_RED));
            MessagesManager.sendMessage(player, message, Prefix.MAILBOX, MessageType.ERROR, true);
            return;
        }
        if (receiverPlayer.getUniqueId() == player.getUniqueId()) {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas vous envoyer à vous-même !", NamedTextColor.DARK_RED), Prefix.MAILBOX, MessageType.ERROR, true);
            return;
        }
        if (!MailboxManager.canSend(player, receiverPlayer)) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas les droits pour envoyer à ", NamedTextColor.DARK_RED)
                            .append(Component.text(receiverPlayer.getName(), NamedTextColor.RED))
                            .append(Component.text(" !", NamedTextColor.DARK_RED)),
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
        LetterHead letterHead = LetterMenu.getById(player, id);
        if (letterHead == null) return;
        LetterMenu mailbox = new LetterMenu(player, letterHead);
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
                    Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                            .append(Component.text(id, NamedTextColor.RED))
                            .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED)),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return;
        }
        MailboxMenuManager.sendConfirmMenuToCancelLetter(player, letter);
    }
}
