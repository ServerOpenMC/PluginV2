package fr.openmc.core.features.mailboxes.menu.letter;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;

public class SendingLetter extends Menu {
    private final OfflinePlayer receiver;
    private boolean hasSent = false;

    public SendingLetter(Player player, OfflinePlayer receiver) {
        super(player);
        this.receiver = receiver;
    }

    public ItemStack[] getItems(Inventory inv) {
        List<ItemStack> itemsList = new ArrayList<>(27);

        for (int slot : MAILBOX_MENU_SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item != null && !item.getType().isAir()) {
                itemsList.add(item);
            }
        }

        return itemsList.toArray(new ItemStack[0]);
    }

    public void sendLetter(Inventory inv) {
        ItemStack[] items = getItems(inv);
        hasSent = true;

        if (items.length == 0) {
            MessagesManager.sendMessage(
                    getOwner(),
                    TranslationManager.translation("feature.mailboxes.message.empty_letter")
                            .color(NamedTextColor.DARK_RED),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return;
        }

        getInventory().clear();
        getOwner().closeInventory();

        sendMailItems(getOwner(), receiver, items);
    }

    private void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
    }

    @Override
    public @NotNull Component getName() {
        String receiverName = receiver.getName() == null ? "" : receiver.getName();
        return TranslationManager.translation(
                "feature.mailboxes.menu.title.sending",
                Component.text(receiverName)
        );
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
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (!hasSent) MailboxManager.givePlayerItems(getOwner(), getItems(inv));
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> items = new HashMap<>();

        List<Integer> slots =
                IntStream.rangeClosed(0, 53)
                        .filter(i -> !MAILBOX_MENU_SLOTS.contains(i))
                        .boxed()
                        .toList();

        for (int slot : slots) {
            items.put(slot, new ItemMenuBuilder(this, ItemUtils.getInvisibleItem()));
        }

        items.put(49, new ItemMenuBuilder(this, getHead(receiver)));
        items.put(45, ItemMenuTemplate.BTN_MAILBOX_HOME.apply(this));
        items.put(48, ItemMenuTemplate.BTN_MAILBOX_SEND.apply(this).setOnClick(e -> sendLetter(e.getInventory())));
        items.put(50, ItemMenuTemplate.BTN_CLOSE.apply(this).setOnClick(_ -> getOwner().closeInventory()));

        return items;
    }

    private static final List<Integer> MAILBOX_MENU_SLOTS =
            IntStream.range(9, 36)
                    .boxed()
                    .toList();

    @Override
    public List<Integer> getTakableSlot() {
        return Stream.concat(MAILBOX_MENU_SLOTS.stream(), MenuUtils.getInventoryItemSlots().stream()).toList();
    }
}