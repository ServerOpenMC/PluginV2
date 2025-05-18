package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.corporation.shops.ShopItem;
import lombok.Getter;

@Getter
@DatabaseTable(tableName = "shop_items")
public class DBShopItem {
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private byte[] items;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private UUID shop;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;

    DBShopItem() {
        // required for ORMLite
    }

    public DBShopItem(byte[] items, UUID shop, double price, int amount) {
        this.items = items;
        this.shop = shop;
        this.price = price;
        this.amount = amount;
    }

    public ShopItem deserialize() {
        ItemStack item = ItemStack.deserializeBytes(items);
        ShopItem shopItem = new ShopItem(item, price);
        shopItem.setAmount(amount);
        return shopItem;
    }
}
