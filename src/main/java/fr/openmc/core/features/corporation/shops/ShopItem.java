package fr.openmc.core.features.corporation.shops;

import fr.openmc.core.utils.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@Getter
public class ShopItem {

    private final UUID itemID;
    private final ItemStack item;
    private final double pricePerItem;
    private double price;
    private int amount;

    public ShopItem(ItemStack item, double pricePerItem) {
        this(item, pricePerItem, UUID.randomUUID());
    }

    public ShopItem(ItemStack item, double pricePerItem, UUID itemID) {
        this.item = item.clone();
        this.pricePerItem = pricePerItem;
        this.item.setAmount(1);
        this.price = pricePerItem * amount;
        this.amount = 0;
        this.itemID = itemID;
    }

    /**
     * get the name of an item
     *
     * @param amount the new amount of the item
     * @return default the ShopItem
     */
    public ShopItem setAmount(int amount) {
        this.amount = amount;
        this.price = pricePerItem * amount;
        return this;
    }

    /**
     * Get the name of an item, either custom or default
     *
     * @param itemStack the item to get the name from
     * @return the name of the item
     */
    public static Component getItemName(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) return itemMeta.displayName();
        }
        // If no custom name, return default name
        return ItemUtils.getItemTranslation(itemStack).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD);
    }

    /**
     * Create a copy of the ShopItem
     *
     * @return the copied ShopItem
     */
    public ShopItem copy() {
        return new ShopItem(item.clone(), pricePerItem);
    }

    /**
     * Get the price of an item based on the amount
     *
     * @param amount the amount of the item
     * @return the total price
     */
    public double getPrice(int amount) {
        return pricePerItem * amount;
    }
}
