package fr.openmc.core.features.adminshop.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopCategory;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminShopCategoryMenu extends Menu {
    private final AdminShopManager shopManager;
    private final String categoryId;

    public AdminShopCategoryMenu(Player owner, AdminShopManager shopManager, String categoryId) {
        super(owner);
        this.shopManager = shopManager;
        this.categoryId = categoryId;
    }

    @Override
    public @NotNull String getName() {
        ShopCategory category = shopManager.getCategory(categoryId);
        return "§f" + PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_adminshop_items%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        Map<String, ShopItem> categoryItems = shopManager.getCategoryItems(categoryId);

        if (categoryItems != null) {
            for (ShopItem item : categoryItems.values()) {
                ItemStack itemStack = new ItemStack(item.getMaterial());
                ItemMeta meta = itemStack.getItemMeta();
                meta.displayName(Component.text(item.getName()));

                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("§aAcheter: $" + String.format("%.2f", item.getActualBuyPrice())));
                lore.add(Component.text("§cVendre: $" + String.format("%.2f", item.getActualSellPrice())));
                lore.add(Component.text("§7"));
                lore.add(Component.text("§8■ §aClique gauche pour §2acheter"));
                lore.add(Component.text("§8■ §cClique droit pour §4vendre"));
                meta.lore(lore);

                itemStack.setItemMeta(meta);

                ItemBuilder itemBuilder = new ItemBuilder(this, itemStack);
                itemBuilder.setItemId(item.getId())
                        .setOnClick(event -> {
                            if (event.isLeftClick()) {
                                shopManager.openBuyConfirmMenu(getOwner(), categoryId, item.getId(), this);
                            } else if (event.isRightClick()) {
                                shopManager.openSellConfirmMenu(getOwner(), categoryId, item.getId(), this);
                            }
                        });

                content.put(item.getSlot(), itemBuilder);
            }
        }

        ItemBuilder backButton = new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("§aRetour au menu principal"));
        });

        backButton.setItemId("back")
                .setOnClick(event -> {
                    new AdminShopMenu(getOwner(), shopManager).open();
                });

        content.put(38, backButton);

        return content;
    }
}