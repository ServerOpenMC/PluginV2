package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.corporation.shops.ShopItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shop_items")
public class DBShopItem {
    @DatabaseField(id = true, columnName = "owner_uuid", canBeNull = false)
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] items;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;
    @DatabaseField(canBeNull = false, columnName = "item_uuid")
    private UUID itemUUID;

    DBShopItem() {
        // required for ORMLite
    }
    
    public DBShopItem(byte[] items, UUID ownerUUID, double price, int amount, UUID itemUUID) {
        this.items = items;
        this.ownerUUID = ownerUUID;
        this.price = price;
        this.amount = amount;
        this.itemUUID = itemUUID;
    }

    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(items);
        ShopItem shopItem = new ShopItem(item, price, itemUUID);
        shopItem.setAmount(amount);
        return shopItem;
    }
}
