package fr.openmc.core.features.events.halloween.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "halloween_data")
public class HalloweenData {
    @DatabaseField(id = true, columnName = "player")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int pumpkinCount;

    HalloweenData() {
        // necessary for OrmLite
    }

    public HalloweenData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.pumpkinCount = 0;
    }

    public void deposit(int amount) {
        pumpkinCount += amount;
    }
}
