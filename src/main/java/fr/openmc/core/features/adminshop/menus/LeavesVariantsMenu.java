package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
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

public class LeavesVariantsMenu extends Menu {
    private final String categoryId;
    private final ShopItem originalItem;

    private static final List<Material> LEAVES_VARIANTS = List.of(
        Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES,
        Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES,
        Material.PALE_OAK_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES
    );

    public LeavesVariantsMenu(Player owner, String categoryId, ShopItem originalItem) {
        super(owner);
        this.categoryId = categoryId;
        this.originalItem = originalItem;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.leaves_variants.name");
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
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33,
                38, 39, 40, 41, 42
        };

        int maxVariants = Math.min(LEAVES_VARIANTS.size(), organizedSlots.length);

        content.put(4, new ItemBuilder(this, originalItem.getMaterial(), meta ->
                meta.displayName(TranslationManager.translation("feature.adminshop.menu.leaves_variants.leaves")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))));

        for (int i = 0; i < maxVariants; i++) {
            Material variant = LEAVES_VARIANTS.get(i);
            int slot = organizedSlots[i];

            TranslatableComponent variantName = ItemUtils.getItemTranslation(variant).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

            content.put(slot, new ItemBuilder(this, variant, meta -> {
                meta.displayName(variantName);
                meta.lore(AdminShopUtils.extractLoreForItem(originalItem));
            }).setItemId(variant.name())
                    .setOnClick(event -> {
                        ShopItem colorVariant = new ShopItem(
                                variant.name(),
                                variantName,
                                variant,
                                originalItem.getSlot(),
                                originalItem.getInitialSellPrice(),
                                originalItem.getInitialBuyPrice(),
                                originalItem.getActualSellPrice(),
                                originalItem.getActualBuyPrice()
                        );


                        if (event.isLeftClick() && originalItem.getInitialBuyPrice() > 0) {
                            AdminShopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                            AdminShopManager.openBuyConfirmMenu(getOwner(), categoryId, colorVariant.getId());
                        } else if (event.isRightClick() && originalItem.getInitialSellPrice() > 0) {
                            AdminShopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                            AdminShopManager.openSellConfirmMenu(getOwner(), categoryId, colorVariant.getId());
                        }
                    }));
        }

        content.put(49, new ItemBuilder(this,
                CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(),
                true));
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
