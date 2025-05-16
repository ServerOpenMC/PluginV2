package fr.openmc.core.features.mailboxes.menu.letter;

import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

public class LetterMenu extends MailboxInv {
    private final static String INV_NAME = "\uF990\uE003";

    static {
        invErrorMessage = "Erreur lors de la récupération de votre boite aux lettres.";
    }

    private final int id;
    private final int itemsCount;
    private ItemStack[] items;

    public LetterMenu(Player player, LetterHead letterHead) {
        super(player);
        this.id = letterHead.getId();
        this.itemsCount = letterHead.getItemsCount();
        this.items = letterHead.getItems();
        if (items != null || getMailboxById()) {
            inventory = Bukkit.createInventory(this, 54, MailboxMenuManager.getInvTitle(INV_NAME));
            inventory.setItem(45, homeBtn());
            inventory.setItem(48, acceptBtn());
            inventory.setItem(49, letterHead);
            inventory.setItem(50, refuseBtn());
            inventory.setItem(53, cancelBtn());

            for (int i = 0; i < items.length; i++)
                inventory.setItem(i + 9, items[i]);
        }
    }

    public static LetterHead getById(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null || letter.isRefused()) {
            sendFailureMessage(player, "La lettre n'a pas été trouvée.");
            return null;
        }
        return letter.toLetterHead();
    }

    public static void refuseLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter != null && !letter.isRefused()) {
            if (letter.refuse()) {
                sendSuccessMessage(player, "La lettre a été refusée.");
                return;
            }
        }

        Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                .append(Component.text(id, NamedTextColor.RED))
                .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
        sendFailureMessage(player, message);
    }

    private boolean getMailboxById() {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null || letter.isRefused())
            return false;

        try {
            items = BukkitSerializer.deserializeItemStacks(letter.getItems());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void accept() {
        if (MailboxManager.deleteLetter(id)) {
            Component message = Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(itemsCount, NamedTextColor.GREEN))
                    .append(Component.text(" " + getItemCount(itemsCount), NamedTextColor.DARK_GREEN));
            sendSuccessMessage(player, message);
            HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(items);
            for (ItemStack item : remainingItems.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        } else {
            Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                    .append(Component.text(id, NamedTextColor.RED))
                    .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
            sendFailureMessage(player, message);
        }
        player.closeInventory();
    }

    public void refuse() {
        Component message = Component.text("Cliquez-ici", NamedTextColor.YELLOW)
                .clickEvent(getRunCommand("refuse " + id))
                .hoverEvent(getHoverEvent("Refuser la lettre #" + id))
                .append(Component.text(" si vous êtes sur de vouloir refuser la lettre.", NamedTextColor.GOLD));
        sendWarningMessage(player, message);
        player.closeInventory();
    }

    public void cancel() {
        player.closeInventory();
        sendFailureMessage(player, "La lettre a été annulée.");
    }
}
