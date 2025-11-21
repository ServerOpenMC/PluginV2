package fr.openmc.core.features.homes.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.homes.HomeLimits;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "home_limits")
public class HomeLimit {

    @DatabaseField(id = true, columnName = "player")
    private UUID playerUUID;
    @Setter
    @DatabaseField(canBeNull = false)
    private int limit;

    HomeLimit() {
        // required for ORMLite
    }

    public HomeLimit(UUID playerUUID, int limit) {
        this.playerUUID = playerUUID;
        this.limit = limit;
    }

    public HomeLimit(UUID playerUUID, HomeLimits limit) {
        this.playerUUID = playerUUID;
        this.limit = limit.getLimit();
    }

    public HomeLimits getHomeLimit() {
        for (HomeLimits value : HomeLimits.values()) {
            if (value.getLimit() == this.limit) {
                return value;
            }
        }
        return null;
    }
}
