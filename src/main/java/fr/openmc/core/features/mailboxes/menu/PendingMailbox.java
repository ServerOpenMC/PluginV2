package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.mailboxes.Letter;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.utils.text.InputUtils.pluralize;

public class PendingMailbox extends PaginatedMenu {
    private final static MailboxManager MAILBOX_MANAGER = OMCRegistry.FEATURES.MAILBOX.get();

    public PendingMailbox(Player player) {
        super(player);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.mailboxes.menu.title.pending");
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

        MAILBOX_MANAGER.getSentLetters(getOwner()).forEach(letter -> {
            items.add(letter.toSenderLetterItemBuilder(this).setOnClick(e -> {
                MAILBOX_MANAGER.sendConfirmMenuToCancelLetter(getOwner(), letter);
            }));
        });

        return items;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> buttons = new HashMap<>();

        buttons.put(45, ItemMenuTemplate.BTN_MAILBOX_HOME.apply(this));
        buttons.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this));
        buttons.put(49, ItemMenuTemplate.BTN_CLOSE.apply(this));
        buttons.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_WHITE.apply(this));

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
        return MAILBOX_MANAGER.getSentLetters(getOwner()).size();
    }

    public static void cancelLetter(Player player, int id) {

    }
}
