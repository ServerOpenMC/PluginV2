package fr.openmc.core.features.homes.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "homes")
public class Limit {

    @DatabaseField(id = true)
    private UUID player;
    @Getter
    @DatabaseField(canBeNull = false)
    private int limit;

    Limit() {
        // required for ORMLite
    }

    public Limit(UUID player, int limit) {
        this.player = player;
        this.limit = limit;
    }
}
