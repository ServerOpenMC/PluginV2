package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.shops.manager.ShopManager;
import fr.openmc.core.utils.bukkit.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shop_items")
public class ShopItem implements Cloneable {
    
    @DatabaseField(id = true, columnName = "shop_uuid", canBeNull = false)
    private UUID shopUUID;
    @DatabaseField(canBeNull = false)
    private double pricePerItem;
    @DatabaseField(canBeNull = false)
    private int amount;
    @DatabaseField(canBeNull = false, columnName = "item_bytes", dataType = DataType.BYTE_ARRAY)
    private byte[] itemBytes;
    
    private double price;
    private ItemStack itemStack;
    
    ShopItem() {
        // required for ORMLite
    }

    public ShopItem(UUID shopUUID, ItemStack itemStack, double pricePerItem) {
        this.shopUUID = shopUUID;
        this.itemStack = itemStack.clone();
        this.pricePerItem = pricePerItem;
        this.amount = 0;
        this.price = pricePerItem * amount;
    }

    /**
     * Set the amount of the ShopItem
     *
     * @param amount the new amount of the item
     * @return default the ShopItem
     */
    public ShopItem setAmount(int amount) {
        this.amount = amount;
        this.price = pricePerItem * amount;
        return this;
    }
    
    public void addAmount(int amount) {
        this.amount += amount;
        this.price = pricePerItem * this.amount;
    }
    
    public void removeAmount(int amount) {
        if (this.amount - amount <= 0) this.amount = 0;
        else this.amount -= amount;
        this.price = pricePerItem * this.amount;
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
     * Get the price of an item based on the amount
     *
     * @param amount the amount of the item
     * @return the total price
     */
    public double getPrice(int amount) {
        return pricePerItem * amount;
    }
    
    public Shop getShop() {
        return ShopManager.getShopByUUID(this.shopUUID);
    }
    
    public ShopItem serialize() {
        this.itemBytes = this.itemStack.serializeAsBytes();
        return this;
    }
    
    public ShopItem deserialize() {
        this.itemStack = ItemStack.deserializeBytes(this.itemBytes);
        return this;
    }
	
	@Override
	public ShopItem clone() {
		try {
			ShopItem clone = (ShopItem) super.clone();
            
            if (this.itemBytes != null) clone.itemBytes = this.itemBytes.clone();
            if (this.itemStack != null) clone.itemStack = this.itemStack.clone();
            
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
}
