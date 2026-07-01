package fr.openmc.core.features.mailboxes.menu.letter;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.events.ClaimLetterEvent;
import fr.openmc.core.features.mailboxes.letter.LetterHead;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.bukkit.serializer.BukkitSerializer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.utils.text.InputUtils.pluralize;

public class LetterMenu extends Menu {
    private final Letter letter;
    private final LetterHead letterHead;
    private ItemStack[] letterItems;

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.mailboxes.menu.title.letter",
                letterHead.displayName()
        );
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

    public static void refuseLetter(Player player, int id) {
        Letter letter = MailboxManager.getById(player, id);
        if (letter != null && !letter.isRefused()) {
            if (letter.refuse()) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.mailboxes.message.refuse_success",
                                Component.text(id).color(NamedTextColor.GREEN)
                        ).color(NamedTextColor.DARK_GREEN),
                        Prefix.MAILBOX,
                        MessageType.SUCCESS,
                        true
                );
                return;
            }
        }

        Component message = TranslationManager.translation(
                "feature.mailboxes.message.letter_not_found",
                Component.text(id).color(NamedTextColor.RED)
        ).color(NamedTextColor.DARK_RED);
        MessagesManager.sendMessage(
                player,
                message,
                Prefix.MAILBOX,
                MessageType.ERROR,
                true
        );
    }

    public void accept() {
        ItemStack[] items = getLetterItems();

        if (MailboxManager.deleteLetter(letterHead.getLetterId())) {
            HashMap<Integer, ItemStack> remainingItems = getOwner().getInventory().addItem(items);
            World world = getOwner().getWorld();
            for (ItemStack item : remainingItems.values()) {
                world.dropItemNaturally(getOwner().getLocation(), item);
            }

            MessagesManager.sendMessage(
                    getOwner(),
                    TranslationManager.translation(
                            "feature.mailboxes.message.items_received",
                            Component.text(letterHead.getItemsCount()).color(NamedTextColor.GREEN),
                            Component.text(pluralize("item", letterHead.getItemsCount())).color(NamedTextColor.DARK_GREEN)
                    ).color(NamedTextColor.DARK_GREEN),
                    Prefix.MAILBOX,
                    MessageType.SUCCESS,
                    true
            );

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new ClaimLetterEvent(getOwner(), letter))
            );


        } else {
            Component message = TranslationManager.translation(
                    "feature.mailboxes.message.letter_not_found",
                    Component.text(letterHead.getLetterId()).color(NamedTextColor.RED)
            ).color(NamedTextColor.DARK_RED);
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

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        ItemStack[] items = getLetterItems();

        if (items == null || items.length == 0) {
            byte[] serializedItems = letter.getItems();
            items = serializedItems != null ? BukkitSerializer.deserializeItemStacks(serializedItems) : new ItemStack[0];
        }

        for (int i = 0; i < items.length; i++)
            content.put(i + 9, new ItemMenuBuilder(this, items[i]));

        content.put(45, ItemMenuTemplate.BTN_MAILBOX_HOME.apply(this));
        content.put(48, ItemMenuTemplate.BTN_MAILBOX_ACCEPT.apply(this)
                .setOnClick(_ -> accept()));
        content.put(49, new ItemMenuBuilder(this, letterHead));
        content.put(50, ItemMenuTemplate.btn(
                this,
                        "✘",
                        "feature.mailboxes.menu.button.refuse",
                        List.of(
                                TranslationManager.translation("feature.mailboxes.menu.refuse_warning")
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)
                                        .decoration(TextDecoration.ITALIC, false)
                        ),
                        OMCRegistry.CUSTOM_ITEMS.MAILBOX_REFUSE_BTN, NamedTextColor.DARK_RED, true)
                .setOnClick(e -> MailboxMenuManager.sendConfirmMenuToCancelLetter(getOwner(), letter)));
        content.put(53, ItemMenuTemplate.BTN_CLOSE.apply(this)
                .setOnClick(_ -> cancel()));

        return content;
    }

    public void cancel() {
        getOwner().closeInventory();
        MessagesManager.sendMessage(
                getOwner(),
                TranslationManager.translation(
                        "feature.mailboxes.message.cancel_letter",
                        Component.text(letterHead.getLetterId()).color(NamedTextColor.RED)
                ).color(NamedTextColor.DARK_RED),
                Prefix.MAILBOX,
                MessageType.ERROR,
                true
        );
    }

    private ItemStack[] getLetterItems() {
        if (letterItems != null) return letterItems;
        ItemStack[] items = letter.getCachedItems();
        if (items == null || items.length == 0) {
            byte[] serializedItems = letter.getItems();
            items = serializedItems != null ? BukkitSerializer.deserializeItemStacks(serializedItems) : new ItemStack[0];
        }
        letterItems = items;
        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
