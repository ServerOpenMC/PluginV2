package fr.openmc.core.features.corporation.shops;

import fr.openmc.core.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@Getter
public class ShopItem {

    private final UUID itemID = UUID.randomUUID();
    private final ItemStack item;
    private final double pricePerItem;
    private double price;
    private int amount;

    public ShopItem(ItemStack item, double pricePerItem) {
        this.item = item.clone();
        this.pricePerItem = pricePerItem;
        this.item.setAmount(1);
        this.price = pricePerItem * amount;
        this.amount = 0;
    }

    public ShopItem setAmount(int amount) {
        this.amount = amount;
        this.price = pricePerItem * amount;
        return this;
    }

    public ShopItem copy() {
        return new ShopItem(item.clone(), pricePerItem);
    }

    public double getPrice(int amount) {
        return pricePerItem * amount;
    }

    public static String getItemName(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                return itemMeta.getDisplayName();
            }
        }
        // If no custom name, return default name
        return String.valueOf(ItemUtils.getDefaultItemName(itemStack));
    }
}
