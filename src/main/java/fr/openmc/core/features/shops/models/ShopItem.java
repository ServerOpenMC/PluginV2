package fr.openmc.core.features.shops.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.shops.manager.ShopManager;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

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
    
    private int maxAmount;
    
    ShopItem() {
        // required for ORMLite
    }

    public ShopItem(UUID shopUUID, ItemStack itemStack, double pricePerItem) {
        this.shopUUID = shopUUID;
        this.itemStack = itemStack.clone();
        this.pricePerItem = pricePerItem;
        this.amount = 0;
        this.price = pricePerItem * amount;
        initMaxAmount();
    }
    
    /**
     * Sets the amount of the ShopItem. If the specified amount exceeds
     * the maximum allowable value (28 full stacks of the item), the amount
     * is capped at the maximum. Also updates the total price based on the
     * new amount.
     *
     * @param amount the desired amount to set for the ShopItem
     * @return the updated ShopItem instance
     */
    public ShopItem setAmount(int amount) {
        if (amount > this.maxAmount) amount = this.maxAmount;
        this.amount = amount;
        this.price = pricePerItem * amount;
        return this;
    }
    
    /**
     * Adjusts the amount of the ShopItem by adding the specified value.
     * If the resulting amount exceeds the maximum permissible value (28 stacks),
     * it is capped at that maximum. Updates the total price based on the new amount.
     *
     * @param amount the amount to be added to the current amount of the item
     */
    public void addAmount(int amount) {
        this.amount += amount;
        if (this.amount > 28 * itemStack.getMaxStackSize()) this.amount = 28 * itemStack.getMaxStackSize();
        this.price = pricePerItem * this.amount;
    }
    
    /**
     * Reduces the amount of this ShopItem by the specified value. If the
     * resulting amount is zero or less, the amount is set to zero. Also
     * updates the total price based on the new amount.
     *
     * @param amount the amount to be subtracted from the current amount
     */
    public void removeAmount(int amount) {
        if (this.amount - amount <= 0) this.amount = 0;
        else this.amount -= amount;
        this.price = pricePerItem * this.amount;
    }
    
    public ShopItem initMaxAmount() {
        this.maxAmount = 28 * itemStack.getMaxStackSize();
        return this;
    }
    
    /**
     * Set a new price per item for this shop item
     *
     * @param pricePerItem the new price per item
     */
    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
        this.price = pricePerItem * amount;
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
    
    /**
     * Retrieves the Shop instance associated with this ShopItem.
     *
     * @return the Shop object corresponding to the shopUUID of this ShopItem
     */
    public Shop getShop() {
        return ShopManager.getShopByUUID(this.shopUUID);
    }
    
    /**
     * Checks whether the current ShopItem instance has reached its maximum allowable
     * stock capacity.
     *
     * @return true if the current amount of the ShopItem is greater than or equal to
     *         the maximum capacity (28 stacks); false otherwise.
     */
    public boolean isFull() {
        return this.amount >= maxAmount;
    }
    
    /**
     * Serializes the itemStack of this ShopItem into a byte array
     * and stores it in the itemBytes field. Returns the updated
     * ShopItem instance.
     *
     * @return the updated ShopItem instance, with the itemBytes field
     *         containing the serialized state of the itemStack
     */
    public ShopItem serialize() {
        this.itemBytes = this.itemStack.serializeAsBytes();
        return this;
    }
    
    /**
     * Deserializes the byte array stored in the itemBytes field of this ShopItem
     * into an ItemStack object and assigns it to the itemStack field.
     * Returns the updated ShopItem instance.
     *
     * @return the updated ShopItem instance with the deserialized ItemStack.
     */
    public ShopItem deserialize() {
        this.itemStack = ItemStack.deserializeBytes(this.itemBytes);
        return this;
    }
    
    /**
     * Creates and returns a copy of this ShopItem object. This method ensures that
     * deep copies of mutable fields, such as itemBytes and itemStack, are also created
     * to avoid unintended side effects from shared references.
     *
     * @return a deep clone of this ShopItem instance
     * @throws AssertionError if the ShopItem object does not support cloning
     */
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
