package fr.openmc.core.features.corporation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "shop_location")
public class DBShopLocation {
    @DatabaseField(id = true, columnName = "owner_uuid", canBeNull = false)
    private UUID ownerUUID;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int y;
    @DatabaseField(canBeNull = false)
    private int z;

    DBShopLocation() {
        // required for ORMLite
    }

    public DBShopLocation(UUID ownerUUID, int x, int y, int z) {
        this.ownerUUID = ownerUUID;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
