package fr.openmc.core.features.mailboxes.menu;


import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.getCustomItem;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;

public class HomeMailbox extends MailboxInv {
    private static final String INV_NAME = "\uF990\uE004";
    
    static OMCPlugin plugin;
    
    public HomeMailbox(Player player, OMCPlugin plugin) {
        super(player);
        HomeMailbox.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 9, MailboxMenuManager.getInvTitle(INV_NAME));
        inventory.setItem(3, getCustomItem(Component.text("En attente", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), 2006));
        inventory.setItem(4, getHead(player, Component.text("Ma boite aux lettres", NamedTextColor.GOLD, TextDecoration.BOLD)));
        inventory.setItem(5, getCustomItem(Component.text("Envoyer", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), 2007));
    }

    public static void openPlayersList(Player player) {
        PlayersList playersList = new PlayersList(player);
        playersList.openInventory();
    }

    public static void openSendingMailbox(Player player, OfflinePlayer receiver, OMCPlugin plugin) throws SQLException {
        SendingLetter sendingLetter = new SendingLetter(player, receiver, plugin);
        sendingLetter.openInventory();
    }

    public static void openPlayerMailbox(Player player) {
        PlayerMailbox playerMailbox = new PlayerMailbox(player);
        playerMailbox.openInventory();
    }

    public static void openPendingMailbox(Player player) {
        PendingMailbox pendingMailbox = new PendingMailbox(player);
        pendingMailbox.openInventory();
    }

    public static void openHomeMailbox(Player player, OMCPlugin plugin) {
        HomeMailbox homeMailbox = new HomeMailbox(player, plugin);
        homeMailbox.openInventory();
    }

    @Override
    public void openInventory() {
        player.openInventory(this.inventory);
    }
}
