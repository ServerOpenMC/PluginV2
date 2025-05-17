package fr.openmc.core.features.homes.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.homes.HomeLimits;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "homes")
public class HomeLimit {

    @Getter
    @DatabaseField(id = true)
    private UUID player;
    @Getter
    @Setter
    @DatabaseField(canBeNull = false)
    private int limit;

    HomeLimit() {
        // required for ORMLite
    }

    public HomeLimit(UUID player, int limit) {
        this.player = player;
        this.limit = limit;
    }

    public HomeLimit(UUID player, HomeLimits limit) {
        this.player = player;
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
