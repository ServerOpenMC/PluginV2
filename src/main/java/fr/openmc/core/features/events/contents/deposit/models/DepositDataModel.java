package fr.openmc.core.features.events.contents.deposit.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "event_deposit_data")
public class DepositDataModel {
    @DatabaseField(id = true, columnName = "id")
    private String id;

    @DatabaseField(canBeNull = false, columnName = "event_id")
    private String eventId;

    @DatabaseField(canBeNull = false, columnName = "player")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, defaultValue = "0", columnName = "deposit_count")
    private int count;

    DepositDataModel() {
        // Necessary for OrmLite
    }

    public DepositDataModel(String eventId, UUID playerUUID) {
        this.id = eventId + ":" + playerUUID;
        this.eventId = eventId;
        this.playerUUID = playerUUID;
        this.count = 0;
    }

    public void deposit(int amount) {
        count += amount;
    }
}

