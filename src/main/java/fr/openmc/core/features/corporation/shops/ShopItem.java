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
        this.item = item.clone();
        this.pricePerItem = pricePerItem;
        this.item.setAmount(1);
        this.price = pricePerItem * amount;
        this.amount = 0;
        this.itemID = UUID.randomUUID();
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
     * copy an ShopItem
     *
     * @return a copy of the ShopItem
     */
    public ShopItem copy() {
        return new ShopItem(item.clone(), pricePerItem);
    }

    /**
     * get the price of a certain amount of an item
     *
     * @param amount amount of item
     * @return a price
     */
    public double getPrice(int amount) {
        return pricePerItem * amount;
    }

    /**
     * get the name of an item
     *
     * @param itemStack the item
     * @return default name if the item has no custom name
     */
    public static Component getItemName(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                return itemMeta.displayName();
            }
        }
        // If no custom name, return default name
        return ItemUtils.getItemTranslation(itemStack).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD);
    }
}
