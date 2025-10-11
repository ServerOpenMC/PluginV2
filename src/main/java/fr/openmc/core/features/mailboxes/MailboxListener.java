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
        if (holder instanceof SendingLetter sendingLetter) sendingLetter.giveItems();
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

    private void runTask(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getView().getTopInventory();
        InventoryHolder holder = inv.getHolder(false);
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        int row = slot / 9;

        if (slot >= inv.getSize()) {
            if (!event.isShiftClick())
                return;

            if (holder instanceof SendingLetter sendingLetter && sendingLetter.noSpace(item)) {
                event.setCancelled(true);
            } else if (holder instanceof MailboxInv) {
                event.setCancelled(true);
            }

            return;
        }

        if (holder instanceof SendingLetter && (row < 1 || row > 3)) {
            event.setCancelled(true);
        } else if (holder instanceof MailboxInv)
            event.setCancelled(true);

        //! Buttons actions
        if (cancelBtn(item) && holder instanceof MailboxInv) {
            runTask(player::closeInventory);
            return;
        } else if (item != null && item.getType() == Material.CHEST && slot == 45 && holder instanceof PlayerMailbox) {
            runTask(() -> HomeMailbox.openHomeMailbox(player, plugin));
            return;
        } else if (holder instanceof PaginatedMailbox<? extends ItemStack> menu) {
            if (nextPageBtn(item)) {
                runTask(menu::nextPage);
                return;
            } else if (previousPageBtn(item)) {
                runTask(menu::previousPage);
                return;
            }
        }

        switch (holder) {
            case SendingLetter sendingLetter when sendBtn(item) -> {
                runTask(sendingLetter::sendLetter);
            }

            case PlayerMailbox playerMailbox when item != null && item.getType() == Material.PLAYER_HEAD -> {
                LetterHead letterHead = playerMailbox.getByIndex(slot);
                if (letterHead == null)
                    return;

                runTask(() -> letterHead.openLetter(player));
            }

            case LetterMenu letterMenu -> {
                if (acceptBtn(item)) {
                    runTask(letterMenu::accept);
                } else if (refuseBtn(item)) {
                    runTask(letterMenu::refuse);
                }
            }

            case HomeMailbox ignored -> {
                if (slot == 3) {
                    runTask(() -> HomeMailbox.openPendingMailbox(player));
                } else if (slot == 4) {
                    runTask(() -> HomeMailbox.openPlayerMailbox(player));
                } else if (slot == 5) {
                    runTask(() -> HomeMailbox.openPlayersList(player));
                }
            }

            case PendingMailbox pendingMailbox when item != null && item.getType() == Material.PLAYER_HEAD -> {
                runTask(() -> pendingMailbox.clickLetter(slot));
            }

            case PlayersList ignored when item != null && item.getType() == Material.PLAYER_HEAD -> {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                OfflinePlayer receiver = meta.getOwningPlayer();
                if (receiver == null)
                    return;

                runTask(() -> {
                    HomeMailbox.openSendingMailbox(player, receiver, OMCPlugin.getInstance());
                });
            }

            case null, default -> {}
        }
    }
}
