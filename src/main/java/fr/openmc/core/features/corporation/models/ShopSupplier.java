package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "shop_suppliers")
@Getter
public class ShopSupplier {
    @DatabaseField(canBeNull = false, id = true, columnName = "owner_uuid")
    private UUID ownerUUID;
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
    
    public ShopSupplier(UUID ownerUUID, UUID item, UUID player, int amount, long time) {
        this.ownerUUID = ownerUUID;
        this.item = item;
        this.player = player;
        this.amount = amount;
        this.time = time;
    }
}
