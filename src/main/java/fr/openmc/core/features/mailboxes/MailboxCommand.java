package fr.openmc.core.features.mailboxes;

import fr.openmc.api.menulib.defaultmenu.ConfirmMenu;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendFailureMessage;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendWarningMessage;

@Command({"mailbox", "mb", "letter", "mail", "lettre", "boite", "courrier"})
@CommandPermission("omc.commands.mailbox")
public class MailboxCommand {
    
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
            sendFailureMessage(player, message);
            // TODO: readd
//        } else if (receiverPlayer.getPlayer() == player) {
//            sendWarningMessage(player, "Vous ne pouvez pas vous envoyer à vous-même !");
        } else if (MailboxManager.canSend(player, receiverPlayer)) {
            new SendingLetter(player, receiverPlayer).open();
        } else {
            sendFailureMessage(player, "Vous n'avez pas les droits pour envoyer à cette personne !");
        }
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
        new ConfirmMenu(player,
                () -> PendingMailbox.cancelLetter(player, id),
                player::closeInventory,
                List.of(Component.text("Confirmer l'annulation de la mailbox #" + id, NamedTextColor.RED)),
                List.of(Component.text("Annuler l'annulation de la mailbox #" + id, NamedTextColor.GREEN))
        ).open();
    }

    @CommandPlaceholder()
    public void mailbox(Player player) {
        new PlayerMailbox(player).open();
    }
}
