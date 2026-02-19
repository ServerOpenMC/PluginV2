package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static com.j256.ormlite.field.DataType.BYTE_ARRAY;

@Getter
@DatabaseTable(tableName = "shop_items")
public class ShopItem {
    
    @DatabaseField(id = true, columnName = "owner_uuid", canBeNull = false)
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false, columnName = "item_uuid")
    private UUID itemUUID;
    @DatabaseField(canBeNull = false)
    private double pricePerItem;
    @DatabaseField(canBeNull = false)
    private int amount;
    @DatabaseField(canBeNull = false, dataType = BYTE_ARRAY)
    private byte[] itemBytes;
    
    private double price;
    private ItemStack item;
    
    ShopItem() {
        // required for ORMLite
    }

    public ShopItem(ItemStack item, double pricePerItem) {
        this(item, pricePerItem, UUID.randomUUID());
    }

    public ShopItem(ItemStack item, double pricePerItem, UUID itemID) {
        this.item = item.clone();
        this.pricePerItem = pricePerItem;
        this.item.setAmount(1);
        this.price = pricePerItem * amount;
        this.amount = 0;
        this.itemUUID = itemID;
    }
    
    public ShopItem(byte[] itemBytes, UUID ownerUUID, double price, int amount, UUID itemUUID) {
        this.itemBytes = itemBytes;
        this.ownerUUID = ownerUUID;
        this.price = price;
        this.amount = amount;
        this.itemUUID = itemUUID;
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
    
    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(itemBytes);
        return new ShopItem(item, price, itemUUID).setAmount(amount);
    }
}
