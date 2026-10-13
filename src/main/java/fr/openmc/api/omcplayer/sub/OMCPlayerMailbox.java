package fr.openmc.api.omcplayer.sub;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.feature.OMCOfflinePlayerFeat;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class OMCPlayerMailbox extends OMCOfflinePlayerFeat {
    protected static final MailboxManager MAILBOX_MANAGER = OMCRegistry.FEATURES.MAILBOX.get();

    public OMCPlayerMailbox(OfflinePlayer player) {
        super(player);
    }

    public boolean sendItems(OMCOfflinePlayer receiver, ItemStack[] items) {
        return MAILBOX_MANAGER.sendItems(getOMCOfflinePlayer(), receiver, items);
    }

    public void sendItemsForce(OMCOfflinePlayer receiver, ItemStack[] items) {
        MAILBOX_MANAGER.sendItemsForce(getPlayer(), receiver, items);
    }

    public boolean sendItems(ItemStack[] items) {
        return MAILBOX_MANAGER.sendItems(getOMCOfflinePlayer(), items);
    }

    public void sendItemsForce(ItemStack[] items) {
        MAILBOX_MANAGER.sendItemsForce(getPlayer(), items);
    }

    public Letter getById(int id) {
        return MAILBOX_MANAGER.getById(getOMCOfflinePlayer(), id);
    }

    public List<Letter> getSentLetters() {
        return MAILBOX_MANAGER.getSentLetters(getOMCOfflinePlayer());
    }

    public List<Letter> getReceivedLetters() {
        return MAILBOX_MANAGER.getReceivedLetters(getOMCOfflinePlayer());
    }

    public boolean canSend(OMCOfflinePlayer receiver) {
        return MAILBOX_MANAGER.canSend(getOMCOfflinePlayer(), receiver);
    }
}
