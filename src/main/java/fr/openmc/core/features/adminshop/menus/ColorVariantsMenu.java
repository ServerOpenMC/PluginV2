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

import java.util.*;

public class ColorVariantsMenu extends Menu {
    private final String categoryId;
    private final ShopItem originalItem;
    private static final Map<String, List<Material>> COLOR_VARIANTS = initColorVariants();

    public ColorVariantsMenu(Player owner, String categoryId, ShopItem originalItem) {
        super(owner);
        this.categoryId = categoryId;
        this.originalItem = originalItem;
    }

    private static Map<String, List<Material>> initColorVariants() {
        Map<String, List<Material>> variants = new HashMap<>();

        List<String> colors = Arrays.asList(
                "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
                "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"
        );

        List<String> types = Arrays.asList(
                "WOOL", "CONCRETE", "CONCRETE_POWDER", "TERRACOTTA", "GLASS", "GLASS_PANE", "STAINED_GLASS", "STAINED_GLASS_PANE"
        );

        for (String type : types) {
            List<Material> materials = new ArrayList<>();

            try {
                materials.add(Material.valueOf(type));
            } catch (IllegalArgumentException ignored) {}

            for (String color : colors) {
                try {
                    materials.add(Material.valueOf(color + "_" + type));
                } catch (IllegalArgumentException ignored) {}
            }

            if (!materials.isEmpty()) {
                variants.put(type, materials);

                if (type.equals("STAINED_GLASS")) variants.put("GLASS", materials);
                if (type.equals("STAINED_GLASS_PANE")) variants.put("GLASS_PANE", materials);
            }
        }

        return variants;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.color_variants.name", ItemUtils.getItemTranslation(originalItem.getMaterial()));
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

        String baseType = originalItem.getBaseType();
        List<Material> variants;
        if (baseType.equals("GLASS")) {
            variants = COLOR_VARIANTS.getOrDefault("STAINED_GLASS", Collections.emptyList());
        } else if (baseType.equals("GLASS_PANE")) {
            variants = COLOR_VARIANTS.getOrDefault("STAINED_GLASS_PANE", Collections.emptyList());
        } else {
            variants = COLOR_VARIANTS.getOrDefault(baseType, Collections.emptyList());
        }

        int[] organizedSlots = {
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33,
                38, 39, 40, 41, 42
        };

        int maxVariants = Math.min(variants.size(), organizedSlots.length);

        content.put(4, new ItemBuilder(this, originalItem.getMaterial(), meta ->
                meta.displayName(ItemUtils.getItemTranslation(originalItem.getMaterial())
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))
        ));

        for (int i = 0; i < maxVariants; i++) {
            Material variant = variants.get(i);
            int slot = organizedSlots[i];

            TranslatableComponent variantName = ItemUtils.getItemTranslation(variant)
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false);

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
