package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.AdminShopUtils;
import fr.openmc.core.features.adminshop.ShopItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AdminShopVariantsMenu extends PaginatedMenu {
    private static final int[] SLOTS = {
            11, 12, 13, 14, 15,
            20, 21, 22, 23, 24,
            29, 30, 31, 32, 33,
            39, 40, 41
    };

    private static final List<Integer> STATIC_SLOTS = IntStream.range(0, 54)
            .filter(s -> Arrays.stream(SLOTS).noneMatch(v -> v == s))
            .boxed()
            .toList();

    private List<ItemStack> itemsCache;
    public final String categoryId;
    public final ShopItem original;

    public AdminShopVariantsMenu(Player owner, String categoryId, ShopItem original) {
        super(owner);
        this.categoryId = categoryId;
        this.original = original;
    }

    public abstract List<Material> getVariants();
    public abstract AdminShopVariantsMenu methodConstructor();

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::adminshop_items:");
    }

    @Override
    public Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return STATIC_SLOTS;
    }

    @Override
    public int getSizeOfItems() {
        return getVariants().size();
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public List<ItemStack> getItems() {
        if (itemsCache == null) {
            List<ItemStack> items = new ArrayList<>();

            for (Material variant : getVariants()) {
                TranslatableComponent variantName = ItemUtils.getItemTranslation(variant)
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false);

                items.add(new ItemMenuBuilder(this, variant, meta -> {
                    meta.displayName(variantName);
                    meta.lore(AdminShopUtils.extractLoreForItem(original));
                }).setOnClick(event -> {
                    ShopItem variantItem = new ShopItem(
                            variant.name(), variantName, variant,
                            original.getSlot(),
                            original.getInitialSellPrice(),
                            original.getInitialBuyPrice(),
                            original.getActualSellPrice(),
                            original.getActualBuyPrice());

                    Runnable back = () -> {
                        AdminShopVariantsMenu menu = methodConstructor();
                        menu.setPage(getPage());
                        menu.open();
                    };

                    AdminShopManager.registerNewItem(categoryId, variantItem.getId(), variantItem);
                    AdminShopManager.handleShopClick(getOwner(), categoryId, variantItem, event, back);
                }));

                itemsCache = items;
            }
        }

        return itemsCache;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> buttons = new HashMap<>();

        buttons.put(4, new ItemMenuBuilder(this, original.getMaterial(), meta ->
                meta.displayName(getName()
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))));

        buttons.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_WHITE.apply(this));
        buttons.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.REFUSE_BTN, true));
        buttons.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_WHITE.apply(this));
        return buttons;
    }
}