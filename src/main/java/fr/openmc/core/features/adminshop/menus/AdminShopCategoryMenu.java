package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminShopCategoryMenu extends Menu {
    private final String categoryId;

    public AdminShopCategoryMenu(Player owner, String categoryId) {
        super(owner);
        this.categoryId = categoryId;
    }

    @Override
    public @NotNull String getName() {
        return "Menu d'une catégorie de l'adminshop";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::adminshop_items:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        Map<String, ShopItem> categoryItems = AdminShopManager.getCategoryItems(categoryId);

        if (categoryItems != null) {
            for (ShopItem item : categoryItems.values()) {
                ItemStack itemStack = new ItemStack(item.getMaterial());
                ItemMeta meta = itemStack.getItemMeta();
                meta.displayName(item.getName().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));

                meta.lore(AdminShopUtils.extractLoreForItem(item));

                itemStack.setItemMeta(meta);

                ItemBuilder itemBuilder = new ItemBuilder(this, itemStack);
                itemBuilder.setItemId(item.getId())
                        .setOnClick(event -> {
                            if (item.getMaterial() == Material.OAK_LEAVES)
                                AdminShopManager.openLeavesVariantsMenu(getOwner(), categoryId, item, this);
                            else if (item.getMaterial() == Material.OAK_LOG)
                                AdminShopManager.openLogVariantsMenu(getOwner(), categoryId, item, this);
                            else if (item.isHasColorVariant())
                                AdminShopManager.openColorVariantsMenu(getOwner(), categoryId, item, this);
                            else if (event.isLeftClick() && item.getInitialBuyPrice() > 0)
                                AdminShopManager.openBuyConfirmMenu(getOwner(), categoryId, item.getId(), this);
                            else if (event.isRightClick() && item.getInitialSellPrice() > 0)
                                AdminShopManager.openSellConfirmMenu(getOwner(), categoryId, item.getId(), this);
                        });

                content.put(item.getSlot(), itemBuilder);
            }
        }

        ItemBuilder backButton = new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("§aRetour au menu principal"));
        }, true);

        content.put(40, backButton);

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}