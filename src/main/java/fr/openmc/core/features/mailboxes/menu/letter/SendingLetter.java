package fr.openmc.core.features.mailboxes.menu.letter;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.sendFailureMessage;

public class SendingLetter extends Menu {
    private final OfflinePlayer receiver;

    public SendingLetter(Player player, OfflinePlayer receiver) {
        super(player);
        this.receiver = receiver;
    }

    public ItemStack[] getItems() {
        List<ItemStack> itemsList = new ArrayList<>(27);
        for (int slot = 9; slot < 36; slot++) {
            ItemStack item = getInventory().getItem(slot);
            if (item != null && !item.getType().isAir()) itemsList.add(item);
        }
        return itemsList.toArray(new ItemStack[0]);
    }

    public void sendLetter() {
        ItemStack[] items = getItems();
        getInventory().clear();
        getOwner().closeInventory();
        if (items.length == 0) {
            sendFailureMessage(getOwner(), "Vous ne pouvez pas envoyer de lettre vide");
            return;
        }

        sendMailItems(getOwner(), receiver, items);
    }

    private void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
    }

    @Override
    public @NotNull String getName() {
        return "Envoyer une lettre à " + receiver.getName();
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::letter_mailbox:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        System.out.println(e.getClick());
        System.out.println(e.getSlot());
        System.out.println(e.getSlotType());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        MailboxManager.givePlayerItems(getOwner(), getItems());
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> items = new HashMap<>();

        for (int i = 0; i < 9; i++) items.put(i, new ItemBuilder(this, MailboxMenuManager.transparentItem(this)));

        items.put(49, new ItemBuilder(this, getHead(receiver)).setOnClick(e -> {}));

        items.put(45, new ItemBuilder(this, MailboxMenuManager.homeBtn(this)));

        items.put(48, new ItemBuilder(this, MailboxMenuManager.sendBtn(this)).setOnClick(e -> sendLetter()));

        items.put(50, new ItemBuilder(this, MailboxMenuManager.cancelBtn(this)).setOnClick(e -> getOwner().closeInventory()));

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of(4);
    }
}