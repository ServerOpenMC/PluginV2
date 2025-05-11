package fr.openmc.core.features.adminshop.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_adminshop_items%");
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

                meta.lore(AdminShopUtils.extractLoreForItem(item));

                itemStack.setItemMeta(meta);

                ItemBuilder itemBuilder = new ItemBuilder(this, itemStack);
                itemBuilder.setItemId(item.getId())
                        .setOnClick(event -> {
                            if (item.isHasColorVariant())
                                shopManager.openColorVariantsMenu(getOwner(), categoryId, item, this);
                            else if (event.isLeftClick() && item.getInitialBuyPrice() > 0)
                                shopManager.openBuyConfirmMenu(getOwner(), categoryId, item.getId(), this);
                            else if (event.isRightClick() && item.getInitialSellPrice() > 0)
                                shopManager.openSellConfirmMenu(getOwner(), categoryId, item.getId(), this);
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

        content.put(40, backButton);

        return content;
    }
}