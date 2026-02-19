package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shop_sales")
public class ShopSale {
    @DatabaseField(canBeNull = false, id = true, columnName = "owner_uuid")
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] items;
    @DatabaseField(canBeNull = false, columnName = "sale_uuid")
    private UUID saleUUID;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;

    ShopSale() {
        // required for ORMLite
    }
    
    public ShopSale(byte[] items, UUID ownerUUID, double price, int amount, UUID saleUUID) {
        this.items = items;
        this.ownerUUID = ownerUUID;
        this.price = price;
        this.amount = amount;
        this.saleUUID = saleUUID;
    }

    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(items);
        ShopItem shopItem = new ShopItem(item, price, saleUUID);
        shopItem.setAmount(amount);
        return shopItem;
    }
}
