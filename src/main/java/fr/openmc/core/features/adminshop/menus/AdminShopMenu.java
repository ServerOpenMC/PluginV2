package fr.openmc.core.features.adminshop.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopCategory;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AdminShopMenu extends Menu {
    private final AdminShopManager shopManager;

    public AdminShopMenu(Player owner, AdminShopManager shopManager) {
        super(owner);
        this.shopManager = shopManager;
    }

    @Override
    public @NotNull String getName() {
        return "§f" + PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-11%%img_adminshop_category%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        int slot = 10;
        for (ShopCategory category : shopManager.getCategories().stream().sorted(Comparator.comparingInt(ShopCategory::position)).toList()) {
            ItemStack itemStack = new ItemStack(category.material());
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component.text(category.name()));
            itemStack.setItemMeta(meta);

            content.put(slot, new ItemBuilder(this, itemStack)
                    .setItemId(category.id())
                    .setOnClick(e -> {
                        shopManager.currentCategory.put(getOwner().getUniqueId(), category.id());
                        new AdminShopCategoryMenu(getOwner(), shopManager, category.id()).open();
                    }));

            slot += 2;
        }

        return content;
    }
}