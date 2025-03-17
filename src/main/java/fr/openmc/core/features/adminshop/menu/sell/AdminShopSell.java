package fr.openmc.core.features.adminshop.menu.sell;

import com.google.common.util.concurrent.AtomicDouble;
import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.adminshop.menu.category.ShopType;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseItems;
import fr.openmc.core.features.adminshop.shopinterfaces.BaseShop;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminShopSell extends Menu {
    BaseItems items;
    private final AtomicInteger number;
    private final AtomicDouble prize;
    private final String material;

    public AdminShopSell(Player player, BaseItems items, String material) {
        super(player);
        this.items = items;
        this.number = new AtomicInteger(1);
        this.prize = new AtomicDouble(items.getSellPrize());
        this.material = material;
    }

    @Override
    public @NotNull String getName() {
        return "§6Admin Shop - §eConfirmation de vente";
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
        for(int i = 0; i < getInventorySize().getSize(); i++) {
            content.put(i, new ItemBuilder(this, Material.BLACK_STAINED_GLASS_PANE, itemMeta -> itemMeta.setDisplayName(" ")));
        }

        content.put(9, createChangeButton(0, Material.BLUE_STAINED_GLASS_PANE, "§9Retirer tous"));
        content.put(10, createChangeButton(-64, Material.PURPLE_STAINED_GLASS_PANE, "§5Retirer 64"));
        content.put(11, createChangeButton(-10, Material.RED_STAINED_GLASS_PANE, "§cRetirer 10"));
        content.put(12, createChangeButton(-1, Material.ORANGE_STAINED_GLASS_PANE, "§cRetirer 1"));

        content.put(13, createItemDisplay());

        content.put(14, createChangeButton(1, Material.LIME_STAINED_GLASS_PANE, "§aAjouter 1"));
        content.put(15, createChangeButton(10, Material.GREEN_STAINED_GLASS_PANE, "§aAjouter 10"));
        content.put(16, createChangeButton(64, Material.PURPLE_STAINED_GLASS_PANE, "§5Ajouter 64"));
        content.put(17, createChangeButton(100, Material.BLUE_STAINED_GLASS_PANE, "§9Ajouter tous"));

        content.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("menu:previous_page").getBest(), ItemMeta -> {
        }).setBackButton());
        content.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("menu:close_button").getBest(), ItemMeta -> {
        }).setCloseButton());

        return content;
    }

    private ItemStack createChangeButton(int amount, Material material, String displayName) {
        return new ItemBuilder(this, material, itemMeta -> itemMeta.setDisplayName(displayName))
                .setOnClick(event -> {
                    int getAmount = BaseShop.getTotalItemsInInventory(items, this.material, getOwner());
                    int newNumber = (amount == 0) ? 1 : (amount == 100) ? getAmount : number.get() + amount;

                    if (newNumber > getAmount) {
                        newNumber = getAmount;
                        getOwner().sendMessage("§cVous ne pouvez pas vendre plus de " + getAmount + " items à la fois.");
                    } else if (newNumber < 1) {
                        newNumber = 1;
                    }

                    number.set(newNumber);
                    updateDisplay(event.getInventory());
                });
    }


    private ItemStack createItemDisplay() {
        return new ItemBuilder(this, Objects.requireNonNull(Material.getMaterial(material == null ? items.named() : items.named() + "_" + material)), itemMeta -> {
            itemMeta.setDisplayName(items.getName());
            updateItemMeta(itemMeta);
        }).setNextMenu(new AdminShopSellConfirm(getOwner(), items, number.get(), material));
    }

    private void updateItemMeta(ItemMeta itemMeta) {
        double finalPrize = prize.get() * number.get();
        itemMeta.setLore(Arrays.asList(
                "  §8■ §7Quantité: §e" + number.get(),
                "  §8■ §7Prix final: §e" + String.format("%.2f", finalPrize) + "$",
                "",
                "§eCliquez pour confirmer la vente !"
        ));
    }

    private void updateDisplay(Inventory inventory) {
        ItemStack itemStack = inventory.getItem(13);
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                updateItemMeta(itemMeta);
                itemStack.setItemMeta(itemMeta);
            }

            inventory.setItem(13, createItemDisplay());
        }
        getOwner().updateInventory();
    }
}
