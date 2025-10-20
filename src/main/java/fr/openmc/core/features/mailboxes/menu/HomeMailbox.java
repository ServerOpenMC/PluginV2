package fr.openmc.core.features.mailboxes.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
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
    public @NotNull String getName() {
        return "Boite aux lettres";
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        content.put(3, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:mailbox_hourglass").getBest(), meta -> {
            meta.displayName(Component
                    .text("En attente", NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }).setOnClick(e -> new PendingMailbox(getOwner()).open()));

        content.put(4, new ItemBuilder(this, getHead(getOwner()), meta -> {
            meta.displayName(Component
                    .text("Ma boite aux lettres", NamedTextColor.GOLD, TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }).setOnClick(e -> new PlayerMailbox(getOwner()).open()));

        content.put(5, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:mailbox_send").getBest(), meta -> {
            meta.displayName(Component
                    .text("Envoyer", NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
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
