package fr.openmc.core.disabled.corporation.models;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.disabled.corporation.shops.ShopItem;
import lombok.Getter;

@Getter
@DatabaseTable(tableName = "shop_sales")
public class DBShopSale {
    @DatabaseField(canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] items;
    @DatabaseField(canBeNull = false)
    private UUID shop;
    @DatabaseField(canBeNull = false, columnName = "sale_uuid")
    private UUID saleUuid;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;

    DBShopSale() {
        // required for ORMLite
    }

    public DBShopSale(byte[] items, UUID shop, double price, int amount, UUID saleUuid) {
        this.items = items;
        this.shop = shop;
        this.price = price;
        this.amount = amount;
        this.saleUuid = saleUuid;
    }

    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(items);
        ShopItem shopItem = new ShopItem(item, price, saleUuid);
        shopItem.setAmount(amount);
        return shopItem;
    }
}
