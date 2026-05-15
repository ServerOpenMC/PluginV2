package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.category.name");
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
                Material material = item.getMaterial();

                boolean category = material.name().endsWith("_LEAVES") || material.name().endsWith("_LOG") || item.isHasColorVariant();

                content.put(item.getSlot(), new ItemBuilder(this, material, meta -> {
                    meta.displayName(item.getName()
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                    meta.lore(category ? List.of(TranslationManager.translation("feature.adminshop.right_click_choice"))
                            : AdminShopUtils.extractLoreForItem(item));
                }).setItemId(item.getId())
                        .setOnClick(event -> {
                            if (material.name().endsWith("_LEAVES"))
                                AdminShopManager.openLeavesVariantsMenu(getOwner(), categoryId, item);
                            else if (material.name().endsWith("_LOG"))
                                AdminShopManager.openLogVariantsMenu(getOwner(), categoryId, item);
                            else if (item.isHasColorVariant())
                                AdminShopManager.openColorVariantsMenu(getOwner(), categoryId, item);
                            else if (event.isLeftClick() && item.getInitialBuyPrice() > 0)
                                AdminShopManager.openBuyConfirmMenu(getOwner(), categoryId, item.getId());
                            else if (event.isRightClick() && item.getInitialSellPrice() > 0)
                                AdminShopManager.openSellConfirmMenu(getOwner(), categoryId, item.getId());
                        }));
            }
        }

        content.put(40, new ItemBuilder(this,
                CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(),
                true));

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}