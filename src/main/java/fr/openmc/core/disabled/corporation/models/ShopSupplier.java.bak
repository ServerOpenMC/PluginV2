package fr.openmc.core.disabled.corporation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "shop_suppliers")
@Getter
public class ShopSupplier {
    @DatabaseField(id = true)
    private UUID id; // différencie les supplier (un joueur peut avoir plusieurs suppliers)
    @DatabaseField(canBeNull = false)
    private UUID shop;
    @DatabaseField(canBeNull = false)
    private UUID item;
    @DatabaseField(canBeNull = false)
    private UUID player;
    @DatabaseField(defaultValue = "0")
    private int amount;
    @DatabaseField(defaultValue = "0")
    private long time;

    ShopSupplier() {
        // required for ORMLite
    }

    public ShopSupplier(UUID id, UUID shop, UUID item, UUID player, int amount, long time) {
        this.id = id;
        this.shop = shop;
        this.item = item;
        this.player = player;
        this.amount = amount;
        this.time = time;
    }
}
