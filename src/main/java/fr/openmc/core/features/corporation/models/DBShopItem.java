package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.corporation.shops.ShopItem;
import lombok.Getter;

@Getter
@DatabaseTable(tableName = "shop_items")
public class DBShopItem {
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] items;
    @DatabaseField(canBeNull = false)
    private UUID shop;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;
    @DatabaseField(canBeNull = false, columnName = "item_uuid")
    private UUID itemUuid;

    DBShopItem() {
        // required for ORMLite
    }

    public DBShopItem(byte[] items, UUID shop, double price, int amount, UUID itemUuid) {
        this.items = items;
        this.shop = shop;
        this.price = price;
        this.amount = amount;
        this.itemUuid = itemUuid;
    }

    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(items);
        ShopItem shopItem = new ShopItem(item, price, itemUuid);
        shopItem.setAmount(amount);
        return shopItem;
    }
}
