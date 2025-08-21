package fr.openmc.core.features.adminshop.menus;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopCategory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminShopMenu extends Menu {

    public AdminShopMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu Principal de l'AdminShop";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-11::adminshop_category:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();

        int slot = 10;
        for (ShopCategory category : AdminShopManager.getCategories().stream().sorted(Comparator.comparingInt(ShopCategory::position)).toList()) {
            ItemStack itemStack = new ItemStack(category.material());
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component.text(category.name()));
            itemStack.setItemMeta(meta);

            content.put(slot, new ItemBuilder(this, itemStack)
                    .setItemId(category.id())
                    .setOnClick(e -> {
                        AdminShopManager.currentCategory.put(getOwner().getUniqueId(), category.id());
                        new AdminShopCategoryMenu(getOwner(), category.id()).open();
                    }));

            slot += 2;
        }

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