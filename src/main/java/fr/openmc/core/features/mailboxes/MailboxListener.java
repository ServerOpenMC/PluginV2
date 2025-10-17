package fr.openmc.core.features.mailboxes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.menu.HomeMailbox;
import fr.openmc.core.features.mailboxes.menu.PendingMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.PlayersList;
import fr.openmc.core.features.mailboxes.menu.letter.LetterMenu;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxInv;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Set;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;

public class MailboxListener implements Listener {
    private final OMCPlugin plugin = OMCPlugin.getInstance();

    @EventHandler
    public void onInventoryOpen(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder(false);
        if (holder instanceof MailboxInv mailboxInv) mailboxInv.addInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder(false);
        if (holder instanceof MailboxInv mailboxInv) mailboxInv.removeInventory();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MailboxManager.sendMailNotification(event.getPlayer());
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inv = event.getView().getTopInventory();
        InventoryHolder holder = inv.getHolder(false);
        Set<Integer> slots = event.getRawSlots();

        if (holder instanceof SendingLetter) {
            for (int slot : slots) {
                if (slot >= 54) continue;
                int row = slot / 9;
                if (row < 1 || row > 3) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (holder instanceof MailboxInv) {
            for (int slot : slots) {
                if (slot >= holder.getInventory().getSize()) continue;
                event.setCancelled(true);
            }
        }
    }

//    @EventHandler
//    public void onClick(InventoryClickEvent event) {
//        switch (holder) {
//            case SendingLetter sendingLetter when sendBtn(item) -> {
//                runTask(sendingLetter::sendLetter);
//            }
//        }
//    }
}
