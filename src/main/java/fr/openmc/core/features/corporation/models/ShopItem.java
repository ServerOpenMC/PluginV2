package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "shop_items")
public class ShopItem {
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private byte[] items;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private UUID shop;
    @DatabaseField(canBeNull = false)
    private double price;
    @DatabaseField(canBeNull = false)
    private int amount;

    ShopItem() {
        // required for ORMLite
    }
}
