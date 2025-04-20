package fr.openmc.core.features.adminshop.menus;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.AdminShopManager;
import fr.openmc.core.features.adminshop.ShopCategory;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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

        Collection<ShopCategory> categories = shopManager.getCategories().stream()
                .sorted((c1, c2) -> Integer.compare(c1.position(), c2.position()))
                .toList();
        int slot = 10;

        for (ShopCategory category : categories) {
            ItemStack itemStack = new ItemStack(category.material());
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component.text(category.name()));
            itemStack.setItemMeta(meta);

            ItemBuilder categoryButton = new ItemBuilder(this, itemStack);
            categoryButton.setItemId(category.id())
                    .setOnClick(event -> {
                        shopManager.currentCategory.put(getOwner().getUniqueId(), category.id());
                        new AdminShopCategoryMenu(getOwner(), shopManager, category.id()).open();
                    });

            content.put(slot, categoryButton);

            slot += 2;
        }

        return content;
    }
}