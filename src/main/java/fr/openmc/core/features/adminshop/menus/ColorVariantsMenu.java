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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ColorVariantsMenu extends Menu {
    private final AdminShopManager shopManager;
    private final String categoryId;
    private final ShopItem originalItem;
    private final Menu previousMenu;
    private static final Map<String, List<Material>> COLOR_VARIANTS = initColorVariants();

    public ColorVariantsMenu(Player owner, AdminShopManager shopManager, String categoryId, ShopItem originalItem, Menu previousMenu) {
        super(owner);
        this.shopManager = shopManager;
        this.categoryId = categoryId;
        this.originalItem = originalItem;
        this.previousMenu = previousMenu;
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
                4,
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33
        };

        int maxVariants = Math.min(variants.size(), organizedSlots.length);

        ItemStack baseItemStack = new ItemStack(originalItem.getMaterial());
        ItemMeta baseMeta = baseItemStack.getItemMeta();
        baseMeta.displayName(Component.text("§7" + getFormattedTypeName(baseType)));
        baseItemStack.setItemMeta(baseMeta);
        content.put(4, baseItemStack);

        for (int i = 0; i < maxVariants; i++) {
            Material variant = variants.get(i);
            int slot = organizedSlots[i];
            if (slot == 4) continue;

            ItemStack itemStack = new ItemStack(variant);
            ItemMeta meta = itemStack.getItemMeta();
            String colorName = AdminShopUtils.getColorNameFromMaterial(variant);

            colorName = colorName.substring(0, 1).toUpperCase() + colorName.substring(1);

            meta.displayName(Component.text("§7" + colorName + " " + getFormattedTypeName(baseType)));

            meta.lore(AdminShopUtils.extractLoreForItem(originalItem));

            itemStack.setItemMeta(meta);

            ItemBuilder itemBuilder = new ItemBuilder(this, itemStack);
            String finalColorName = colorName;
            itemBuilder.setItemId(variant.name())
                    .setOnClick(event -> {
                        ShopItem colorVariant = new ShopItem(
                                variant.name(),
                                "§7" + finalColorName + " " + getFormattedTypeName(baseType),
                                variant,
                                originalItem.getSlot(),
                                originalItem.getInitialSellPrice(),
                                originalItem.getInitialBuyPrice(),
                                originalItem.getActualSellPrice(),
                                originalItem.getActualBuyPrice()
                        );


                       if (event.isLeftClick() && originalItem.getInitialBuyPrice() > 0) {
                           shopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                           shopManager.openBuyConfirmMenu(getOwner(), categoryId, colorVariant.getId(), this);
                       } else if (event.isRightClick() && originalItem.getInitialSellPrice() > 0) {
                           shopManager.registerNewItem(categoryId, colorVariant.getId(), colorVariant);
                           shopManager.openSellConfirmMenu(getOwner(), categoryId, colorVariant.getId(), this);
                       }
                    });

            content.put(slot, itemBuilder);
        }

        ItemBuilder backButton = new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), meta -> {
            meta.displayName(Component.text("§aRetour à la catégorie"));
        });

        backButton.setItemId("back")
                .setOnClick(event -> {
                    previousMenu.open();
                });

        content.put(49, backButton);

        return content;
    }

    /**
     * Returns a formatted type name based on the base type.
     * This is used to display the item type in a user-friendly way.
     *
     * @param baseType The base type of the item (e.g., WOOL, CONCRETE).
     * @return A formatted string representing the type name.
     */
    private String getFormattedTypeName(String baseType) {
        return switch (baseType) {
            case "WOOL" -> "Laine";
            case "CONCRETE" -> "Béton";
            case "CONCRETE_POWDER" -> "Béton en poudre";
            case "TERRACOTTA" -> "Terre cuite";
            case "GLASS" -> "Verre";
            case "GLASS_PANE" -> "Vitre";
            case "CARPET" -> "Tapis";
            case "BED" -> "Lit";
            case "SHULKER_BOX" -> "Boîte de Shulker";
            case "GLAZED_TERRACOTTA" -> "Terre cuite émaillée";
            case "BANNER" -> "Bannière";
            case "STAINED_GLASS" -> "Verre teinté";
            case "STAINED_GLASS_PANE" -> "Vitre teintée";
            case "CANDLE" -> "Bougie";
            default -> baseType.toLowerCase();
        };
    }
}
