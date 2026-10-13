package fr.openmc.core.features.city.sub.notation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "notations_activity")
@Getter
@Setter
public class ActivityTimePlayed {
    @DatabaseField(id = true, columnName = "player_uuid")
    private UUID playerUUID;
    @DatabaseField
    private long timeOnWeekStart;

    ActivityTimePlayed() {
        // required for ORMLite
    }

    public ActivityTimePlayed(UUID playerUUID, long timeOnWeekStart) {
        this.playerUUID = playerUUID;
        this.timeOnWeekStart = timeOnWeekStart;
    }
}
