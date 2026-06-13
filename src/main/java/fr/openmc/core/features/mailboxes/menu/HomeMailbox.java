package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;

public class HomeMailbox extends Menu {

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.mailboxes.menu.title.home");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::home_mailbox:");
    }
    
    public HomeMailbox(Player player) {
        super(player);
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.SMALLEST;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(3, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.MAILBOX_HOURGLASS, meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.pending.item")
                    .color(NamedTextColor.DARK_AQUA)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }).setOnClick(e -> new PendingMailbox(getOwner()).open()));

        content.put(4, new ItemMenuBuilder(this, getHead(getOwner()), meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.player.item")
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }).setOnClick(e -> new PlayerMailbox(getOwner()).open()));

        content.put(5, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.MAILBOX_SEND, meta -> {
            meta.displayName(TranslationManager.translation("feature.mailboxes.menu.send.item")
                    .color(NamedTextColor.DARK_AQUA)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }).setOnClick(e -> new PlayersList(getOwner()).open()));

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
