package fr.openmc.core.features.events.contents.christmas.gingerbread.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "chistmas_gingerbread_data")
public class GingerbreadData {
    @DatabaseField(id = true, columnName = "player")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, defaultValue = "0", columnName = "gingerbrean_count")
    private int gingerbreadCount;

    GingerbreadData() {
        // Necessary for OrmLite
    }

    public GingerbreadData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.gingerbreadCount = 0;
    }

    public void depositPumpkins(int amount) {
        gingerbreadCount += amount;
    }
}
