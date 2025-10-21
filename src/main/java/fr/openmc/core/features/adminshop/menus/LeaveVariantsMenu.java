package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LeaveVariantsMenu extends Menu {
    private final String categoryId;
    private final ShopItem originalItem;
    private final Menu previousMenu;
    private static final List<Material> LEAVES_VARIANTS = List.of(
        Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES,
        Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES,
        Material.PALE_OAK_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES
    );

    public LeaveVariantsMenu(Player owner, String categoryId, ShopItem originalItem, Menu previousMenu) {
        super(owner);
        this.categoryId = categoryId;
        this.originalItem = originalItem;
        this.previousMenu = previousMenu;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des variantes de feuilles";
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        int[] organizedSlots = {
                4,
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33
        };

        int maxVariants = Math.min(LEAVES_VARIANTS.size(), organizedSlots.length);

        ItemStack baseItemStack = new ItemStack(originalItem.getMaterial());
        ItemMeta baseMeta = baseItemStack.getItemMeta();
        baseMeta.displayName(Component.text("Feuilles", NamedTextColor.GRAY));
        baseItemStack.setItemMeta(baseMeta);
        content.put(4, new ItemBuilder(this, baseItemStack));

        for (int i = 0; i < maxVariants; i++) {
            Material variant = LEAVES_VARIANTS.get(i);
            int slot = organizedSlots[i];
            if (slot == 4) continue;

            ItemStack itemStack = new ItemStack(variant);
            ItemMeta meta = itemStack.getItemMeta();

            meta.displayName(ItemUtils.getItemTranslation(variant).color(NamedTextColor.GRAY));

            meta.lore(AdminShopUtils.extractLoreForItem(originalItem));

            itemStack.setItemMeta(meta);

            ItemBuilder itemBuilder = new ItemBuilder(this, itemStack);
            itemBuilder.setItemId(variant.name())
                    .setOnClick(event -> {
                        ShopItem colorVariant = new ShopItem(
                                variant.name(),
                                ItemUtils.getItemTranslation(variant).color(NamedTextColor.GRAY),
                                variant,
                                originalItem.getSlot(),
                                originalItem.getInitialSellPrice(),
                                originalItem.getInitialBuyPrice(),
                                originalItem.getActualSellPrice(),
                                originalItem.getActualBuyPrice()
                        );


                       if (event.isLeftClick() && originalItem.getInitialBuyPrice() > 0) {
                           AdminShopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                           AdminShopManager.openBuyConfirmMenu(getOwner(), categoryId, colorVariant.getId(), this);
                       } else if (event.isRightClick() && originalItem.getInitialSellPrice() > 0) {
                           AdminShopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                           AdminShopManager.openSellConfirmMenu(getOwner(), categoryId, colorVariant.getId(), this);
                       }
                    });

            content.put(slot, itemBuilder);
        }

        ItemBuilder backButton = new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("Retour à la catégorie", NamedTextColor.GREEN));
        });

        backButton.setItemId("back")
                .setOnClick(event -> {
                    previousMenu.open();
                });

        content.put(49, backButton);

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
