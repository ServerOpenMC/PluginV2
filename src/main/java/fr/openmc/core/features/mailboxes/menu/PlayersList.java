package fr.openmc.core.features.mailboxes.menu;


import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.features.mailboxes.utils.PaginatedMailbox;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayersList extends PaginatedMenu {
    public PlayersList(Player player) {
        super(player);
    }

    @Override
    public @NotNull String getName() {
        return "Liste des joueurs";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§f§r:offset_-8::player_mailbox:");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> pageItems = new java.util.ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == getOwner()) continue;
            if (!MailboxManager.canSend(getOwner(), onlinePlayer)) continue;
            pageItems.add(ItemUtils.getPlayerHead(onlinePlayer.getUniqueId()));
        }
        return pageItems;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> buttons = new HashMap<>();

        buttons.put(45, new ItemBuilder(this, Material.CHEST, meta -> {
            meta.displayName(Component.text("§6§l⬅ Home"));
        }).setOnClick(e -> {
            new HomeMailbox(getOwner()).open();
        }));

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
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public int getSizeOfItems() {
        return Bukkit.getOnlinePlayers().size() - 1;
    }
}
