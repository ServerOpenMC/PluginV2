package fr.openmc.core.features.mailboxes.utils;


import fr.openmc.core.OMCPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.playerInventories;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendFailureMessage;

public abstract class MailboxInv implements InventoryHolder {
    protected static String invErrorMessage;
    protected final Player player;
    protected final OMCPlugin plugin = OMCPlugin.getInstance();
    protected Inventory inventory;

    public MailboxInv(Player player) {
        this.player = player;
    }

    public void addInventory() {
        playerInventories.put(player, this);
    }

    public void removeInventory() {
        playerInventories.remove(player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void openInventory() {
        if (inventory == null) {
            sendInvErrorMessage(player);
            return;
        }
        player.openInventory(this.inventory);
    }

    protected void sendInvErrorMessage(Player player) {
        if (invErrorMessage == null) return;
        sendFailureMessage(player, invErrorMessage);
    }
}
