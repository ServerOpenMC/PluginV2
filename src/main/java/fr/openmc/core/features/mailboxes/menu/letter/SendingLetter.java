package fr.openmc.core.features.mailboxes.menu.letter;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendFailureMessage;

public class SendingLetter extends MailboxInv {
    private final static String INV_NAME = "\uF990\uE003";
    private final OfflinePlayer receiver;
    private final OMCPlugin plugin;
    private List<String> playerFriends = new ArrayList<>();

    int mailAccept;

    public SendingLetter(Player player, OfflinePlayer receiver, OMCPlugin plugin) throws SQLException {
        super(player);
        this.receiver = receiver;
        this.plugin = plugin;
        inventory = Bukkit.createInventory(this, 54, MailboxMenuManager.getInvTitle(INV_NAME));
        inventory.setItem(49, getHead(receiver));
        inventory.setItem(45, homeBtn());
        inventory.setItem(48, sendBtn());
        inventory.setItem(50, cancelBtn());

        for (int i = 0; i < 9; i++) inventory.setItem(i, transparentItem());

    }

    @Override
    public void openInventory() {
        player.openInventory(this.inventory);
    }

    public ItemStack[] getItems() {
        List<ItemStack> itemsList = new ArrayList<>(27);
        for (int slot = 9; slot < 36; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.getType().isAir()) itemsList.add(item);
        }
        return itemsList.toArray(new ItemStack[0]);
    }

    public void sendLetter() {
        ItemStack[] items = getItems();
        inventory.clear();
        player.closeInventory();
        if (items.length == 0) {
            sendFailureMessage(player, "Vous ne pouvez pas envoyer de lettre vide");
            return;
        }

        sendMailItems(player, receiver, items);
    }

    private void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!MailboxManager.sendItems(player, receiver, items)) {
                MailboxManager.givePlayerItems(player, items);
            }
        });
    }

    public void giveItems() {
        MailboxManager.givePlayerItems(player, getItems());
    }

    public boolean noSpace(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        int size = item.getAmount();
        for (int slot = 9; slot < 36; slot++) {
            ItemStack targetItem = inventory.getItem(slot);
            if (targetItem == null || targetItem.getType().isAir()) return false;
            if (targetItem.isSimilar(item)) size -= targetItem.getMaxStackSize() - targetItem.getAmount();
            if (size <= 0) return false;
        }
        return true;
    }
}