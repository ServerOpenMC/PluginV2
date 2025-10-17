package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.defaultmenu.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.letter.SenderLetter;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;

public class PendingMailbox extends PaginatedMenu {
    public PendingMailbox(Player player) {
        super(player);
    }

    @Override
    public @NotNull String getName() {
        return "Courriers en attente d'annulation";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::player_mailbox:");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        MailboxManager.getSentLetters(getOwner()).forEach(letter -> {
            items.add(letter.toSenderLetterItemBuilder(this).setOnClick(e -> {
                new ConfirmMenu(getOwner(),
                        () -> {
                            PendingMailbox.cancelLetter(getOwner(), letter.getLetterId());
                            new PendingMailbox(getOwner()).open();
                            MessagesManager.sendMessage(
                                    getOwner(),
                                    Component.text("Vous avez annulé la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN),
                                    Prefix.MAILBOX,
                                    MessageType.SUCCESS,
                                    false
                            );
                        },
                        () -> getOwner().closeInventory(),
                        List.of(Component.text("Confirmer l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.RED)),
                        List.of(Component.text("Annuler l'annulation de la mailbox #" + letter.getLetterId(), NamedTextColor.GREEN))
                ).open();
            }));
        });

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(45, MailboxMenuManager.homeBtn(this));

        buttons.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_left").getBest(), meta -> {
            meta.displayName(Component.text("§6§l⬅ Previous Page"));
        }).setPreviousPageButton());

        buttons.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:mailbox_cancel_btn").getBest(), meta -> {
            meta.displayName(Component.text("§8§l[§c§l✖§8§l] §c§lClose"));
        }).setCloseButton());

        buttons.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:mailbox_arrow_right").getBest(), meta -> {
            meta.displayName(Component.text("§6§lNext Page ➡"));
        }).setNextPageButton());

        return buttons;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public int getSizeOfItems() {
        return MailboxManager.getSentLetters(getOwner()).size();
    }

    public static void cancelLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null) {
            Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                    .append(Component.text(id, NamedTextColor.RED))
                    .append(Component.text(" n'a pas été trouvée.", NamedTextColor.DARK_RED));
            sendFailureMessage(player, message);
        }

        int itemsCount = letter.getNumItems();
        ItemStack[] items = BukkitSerializer.deserializeItemStacks(letter.getItems());
        Player receiver = CacheOfflinePlayer.getOfflinePlayer(letter.getReceiver()).getPlayer();

        if (MailboxManager.deleteLetter(id)) {
            if (receiver != null)
                MailboxManager.cancelLetter(receiver, id);
            MailboxManager.givePlayerItems(player, items);
            Component message = Component.text("Vous avez annulé la lettre et reçu ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(itemsCount, NamedTextColor.GREEN))
                    .append(Component.text(" " + getItemCount(itemsCount), NamedTextColor.DARK_GREEN));
            sendSuccessMessage(player, message);
        }
    }
}
