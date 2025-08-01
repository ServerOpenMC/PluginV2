package fr.openmc.core.features.mailboxes;

import fr.openmc.core.OMCPlugin;
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

import java.sql.SQLException;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendFailureMessage;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendWarningMessage;

@Command({"mailbox", "mb", "letter", "mail", "lettre", "boite", "courrier"})
@CommandPermission("omc.commands.mailbox")
public class MailboxCommand {
    
    private OMCPlugin plugin;
    
    public MailboxCommand(OMCPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Subcommand("home")
    @Description("Ouvrir la page d'accueil de la boite aux lettres")
    public static void homeMailbox(Player player) {
        HomeMailbox homeMailbox = new HomeMailbox(player, OMCPlugin.getInstance());
        homeMailbox.openInventory();
    }

    @Subcommand("send")
    @Description("Envoyer une lettre à un joueur")
    @AutoComplete("@players")
    public void sendMailbox(Player player, @Named("player") String receiver) throws SQLException {
        OfflinePlayer receiverPlayer = Bukkit.getPlayerExact(receiver);
        if (receiverPlayer == null) receiverPlayer = Bukkit.getOfflinePlayerIfCached(receiver);
        if (receiverPlayer == null || !(receiverPlayer.hasPlayedBefore() || receiverPlayer.isOnline())) {
            Component message = Component.text("Le joueur ", NamedTextColor.DARK_RED)
                                         .append(Component.text(receiver, NamedTextColor.RED))
                                         .append(Component.text(" n'existe pas ou ne s'est jamais connecté !", NamedTextColor.DARK_RED));
            sendFailureMessage(player, message);
        } else if (receiverPlayer.getPlayer() == player) {
            sendWarningMessage(player, "Vous ne pouvez pas vous envoyer à vous-même !");
        } else if (MailboxManager.canSend(player, receiverPlayer)) {
            SendingLetter sendingLetter = new SendingLetter(player, receiverPlayer, plugin);
            sendingLetter.openInventory();
        } else {
            sendFailureMessage(player, "Vous n'avez pas les droits pour envoyer à cette personne !");
        }
    }

    @Subcommand("pending")
    @Description("Ouvrir les lettres en attente de réception")
    public void pendingMailbox(Player player) {
        PendingMailbox pendingMailbox = new PendingMailbox(player);
        pendingMailbox.openInventory();
    }

    @SecretCommand
    @Subcommand("open")
    @Description("Ouvrir une lettre")
    public void openMailbox(Player player, @Named("id") @Range(min = 1, max = Integer.MAX_VALUE) int id) {
        LetterHead letterHead = LetterMenu.getById(player, id);
        if (letterHead == null) return;
        LetterMenu mailbox = new LetterMenu(player, letterHead);
        mailbox.openInventory();
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
        PendingMailbox.cancelLetter(player, id);
    }

    @DefaultFor("~")
    public void mailbox(Player player) {
        new PlayerMailbox(player).openInventory();
    }
}
