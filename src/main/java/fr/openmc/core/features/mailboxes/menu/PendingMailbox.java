package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
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

import static fr.openmc.core.utils.InputUtils.pluralize;

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
                MailboxMenuManager.sendConfirmMenuToCancelLetter(getOwner(), letter);
            }));
        });

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(45, MailboxMenuManager.homeBtn(this));
        buttons.putAll(MailboxMenuManager.getPaginatedButtons(this));

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
            MessagesManager.sendMessage(
                    player,
                    message,
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
        }

        int itemsCount = letter.getNumItems();
        ItemStack[] items = BukkitSerializer.deserializeItemStacks(letter.getItems());
        Player receiver = CacheOfflinePlayer.getOfflinePlayer(letter.getReceiver()).getPlayer();

        if (MailboxManager.deleteLetter(id)) {
            if (receiver != null)
                MailboxManager.cancelLetter(receiver);
            MailboxManager.givePlayerItems(player, items);
            Component message = Component.text("Vous avez annulé la lettre et reçu ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(itemsCount, NamedTextColor.GREEN))
                    .append(Component.text(" " + pluralize("item", itemsCount), NamedTextColor.DARK_GREEN));

            MessagesManager.sendMessage(
                    player,
                    message,
                    Prefix.MAILBOX,
                    MessageType.SUCCESS,
                    true
            );
        }
    }
}
