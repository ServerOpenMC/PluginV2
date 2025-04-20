package fr.openmc.core.features.adminshop.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
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
                "WOOL", "CONCRETE", "CONCRETE_POWDER", "TERRACOTTA", "GLASS", "GLASS_PANE",
                "CARPET", "BED", "SHULKER_BOX", "GLAZED_TERRACOTTA", "BANNER", "STAINED_GLASS",
                "STAINED_GLASS_PANE", "CANDLE"
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
        List<Material> variants = COLOR_VARIANTS.getOrDefault(baseType, Collections.emptyList());

        int slot = 10;
        for (Material variant : variants) {
            ItemStack itemStack = new ItemStack(variant);
            ItemMeta meta = itemStack.getItemMeta();
            String materialName = variant.name();
            String colorName = materialName.contains("_") ?
                    materialName.substring(0, materialName.indexOf("_")).toLowerCase() :
                    "normal";

            colorName = colorName.substring(0, 1).toUpperCase() + colorName.substring(1);

            meta.displayName(Component.text("§7" + colorName + " " + getFormattedTypeName(baseType)));

            List<Component> lore = new ArrayList<>();
            if (originalItem.getInitialBuyPrice() > 0 && originalItem.getInitialSellPrice() <= 0) {
                lore.add(Component.text("§aAcheter: $" + String.format("%.2f", originalItem.getActualBuyPrice())));
                lore.add(Component.text("§7"));
                lore.add(Component.text("§8■ §aClique gauche pour §2acheter"));
            } else if (originalItem.getInitialSellPrice() > 0 && originalItem.getInitialBuyPrice() <= 0) {
                lore.add(Component.text("§cVendre: $" + String.format("%.2f", originalItem.getActualSellPrice())));
                lore.add(Component.text("§7"));
                lore.add(Component.text("§8■ §cClique droit pour §4vendre"));
            } else {
                lore.add(Component.text("§aAcheter: $" + String.format("%.2f", originalItem.getActualBuyPrice())));
                lore.add(Component.text("§cVendre: $" + String.format("%.2f", originalItem.getActualSellPrice())));
                lore.add(Component.text("§7"));
                lore.add(Component.text("§8■ §aClique gauche pour §2acheter"));
                lore.add(Component.text("§8■ §cClique droit pour §4vendre"));
            }
            meta.lore(lore);

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
                            shopManager.openBuyConfirmMenu(getOwner(), categoryId, variant.name(), this);
                            shopManager.addTemporaryItem(categoryId, variant.name(), colorVariant);
                        } else if (event.isRightClick() && originalItem.getInitialSellPrice() > 0) {
                            shopManager.openSellConfirmMenu(getOwner(), categoryId, variant.name(), this);
                            shopManager.addTemporaryItem(categoryId, variant.name(), colorVariant);
                        }
                    });

            content.put(slot++, itemBuilder);

            if ((slot - 9) % 9 == 0) {
                slot += 2;
            }
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