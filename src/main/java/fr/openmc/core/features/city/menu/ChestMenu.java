package fr.openmc.core.features.city.menu;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ChestMenu {
    private final City city;
    @Getter private final int page;
    private static ItemStack border;
    @Getter @Setter private Inventory inventory;

    private static ItemStack getBorder() {
        if (border != null) {
            return border.clone();
        }
        border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        ItemMeta meta = border.getItemMeta();
        meta.displayName(Component.empty());
        border.setItemMeta(meta);

        return border.clone();
    }

    public ChestMenu(City city, int page) {
        this.city = city;
        this.page = page;

        if (this.page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }

        if (this.page > this.city.getChestPages()) {
            throw new IllegalArgumentException("Page must be less than or equal to " + this.city.getChestPages());
        }
    }

    public boolean hasNextPage() {
        return this.page < this.city.getChestPages();
    }

    public boolean hasPreviousPage() {
        return this.page > 1;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Component.text("Banque de " + this.city.getName() + " - Page " + this.page));

        inventory.setContents(this.city.getChestContent(this.page));

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, getBorder());
        }

        if (hasPreviousPage()) {
            ItemStack previous = CustomItemRegistry.getByName("menu:previous_page").getBest();
            inventory.setItem(45, previous);
        }

        if (hasNextPage()) {
            ItemStack next = CustomItemRegistry.getByName("menu:next_page").getBest();
            inventory.setItem(53, next);
        }

        if (city.hasPermission(player.getUniqueId(), CPermission.CHEST_UPGRADE) && !(city.getChestPages() >= 5)) {
            ItemStack upgrade = new ItemStack(Material.ENDER_CHEST);
            ItemMeta meta = upgrade.getItemMeta();
            meta.displayName(Component.text("§aAméliorer le coffre"));
            meta.lore(List.of(
                    Component.text("§7Votre ville doit avoir " + city.getChestPages()*5000 + EconomyManager.getEconomyIcon()),
                    Component.text("§e§lCLIQUEZ ICI POUR AMELIORER LE COFFRE")
            ));
            upgrade.setItemMeta(meta);
            inventory.setItem(48, upgrade);
        }

        ItemStack next = CustomItemRegistry.getByName("menu:close_button").getBest();
        inventory.setItem(49, next);

        player.openInventory(inventory);
        city.setChestWatcher(player.getUniqueId());
        city.setChestMenu(this);
        this.inventory = inventory;
    }
}