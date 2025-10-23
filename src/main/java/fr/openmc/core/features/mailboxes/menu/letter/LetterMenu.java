package fr.openmc.core.features.mailboxes.menu.letter;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.events.ClaimLetterEvent;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.mailboxes.utils.MailboxMenuManager.*;
import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.*;
import static fr.openmc.core.utils.InputUtils.pluralize;

public class LetterMenu extends Menu {
    private final Letter letter;
    private final LetterHead letterHead;

    @Override
    public @NotNull String getName() {
        return "Lettre de " + letterHead.displayName();
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::letter_mailbox:");
    }

    public LetterMenu(Player player, Letter letter) {
        super(player);
        this.letter = letter;
        this.letterHead = letter.toLetterHead();
    }

    public static LetterHead getById(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter == null || letter.isRefused()) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("La lettre avec #", NamedTextColor.DARK_RED)
                            .append(Component.text(id, NamedTextColor.RED))
                            .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED)),
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
            return null;
        }
        return letter.toLetterHead();
    }

    public static void refuseLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter != null && !letter.isRefused()) {
            if (letter.refuse()) {
                MessagesManager.sendMessage(
                        player,
                        Component.text("Vous avez refusé la lettre #", NamedTextColor.DARK_GREEN)
                                .append(Component.text(id, NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.DARK_GREEN)),
                        Prefix.MAILBOX,
                        MessageType.SUCCESS,
                        true
                );
                return;
            }
        }

        Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                .append(Component.text(id, NamedTextColor.RED))
                .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
        MessagesManager.sendMessage(
                player,
                message,
                Prefix.MAILBOX,
                MessageType.ERROR,
                true
        );
    }

    public void accept() {
        if (MailboxManager.deleteLetter(letterHead.getLetterId())) {
            MessagesManager.sendMessage(
                    getOwner(),
                    Component.text("Vous avez reçu ", NamedTextColor.DARK_GREEN)
                            .append(Component.text(letterHead.getItemsCount(), NamedTextColor.GREEN))
                            .append(Component.text(" " + pluralize("item", letterHead.getItemsCount()), NamedTextColor.DARK_GREEN)),
                    Prefix.MAILBOX,
                    MessageType.SUCCESS,
                    true
            );

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new ClaimLetterEvent(getOwner(), MailboxManager.getById(getOwner(), letterHead.getLetterId())))
            );

            HashMap<Integer, ItemStack> remainingItems = getOwner().getInventory().addItem(letter.getCachedItems());
            for (ItemStack item : remainingItems.values()) {
                getOwner().getWorld().dropItemNaturally(getOwner().getLocation(), item);
            }
        } else {
            Component message = Component.text("La lettre avec l'id ", NamedTextColor.DARK_RED)
                    .append(Component.text(letterHead.getLetterId(), NamedTextColor.RED))
                    .append(Component.text(" n'existe pas.", NamedTextColor.DARK_RED));
            MessagesManager.sendMessage(
                    getOwner(),
                    message,
                    Prefix.MAILBOX,
                    MessageType.ERROR,
                    true
            );
        }
        getOwner().closeInventory();
    }

    public void refuse() {
        Component message = Component.text("Cliquez-ici", NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mailbox refuse " + letterHead.getLetterId()))
                .hoverEvent(getHoverEvent("Refuser la lettre #" + letterHead.getLetterId()))
                .append(Component.text(" si vous êtes sur de vouloir refuser la lettre.", NamedTextColor.GOLD));

        MessagesManager.sendMessage(
                getOwner(),
                message,
                Prefix.MAILBOX,
                MessageType.WARNING,
                true
        );
        getOwner().closeInventory();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        ItemStack[] items = letter.getCachedItems();

        for (int i = 0; i < items.length; i++)
            content.put(i + 9, new ItemBuilder(this, items[i]));

        content.put(45, homeBtn(this));

        content.put(48, acceptBtn(this).setOnClick(e -> accept()));

        content.put(49, new ItemBuilder(this, letterHead));

        content.put(50, refuseBtn(this).setOnClick(e -> MailboxMenuManager.sendConfirmMenuToCancelLetter(getOwner(), letter)));

        content.put(53, cancelBtn(this).setOnClick(e -> cancel()));

        return content;
    }

    public void cancel() {
        getOwner().closeInventory();
        MessagesManager.sendMessage(
                getOwner(),
                Component.text("Vous avez annulé la lettre #", NamedTextColor.DARK_RED)
                        .append(Component.text(letterHead.getLetterId(), NamedTextColor.RED))
                        .append(Component.text(".", NamedTextColor.DARK_RED)),
                Prefix.MAILBOX,
                MessageType.ERROR,
                true
        );
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
