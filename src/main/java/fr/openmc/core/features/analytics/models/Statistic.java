package fr.openmc.core.features.analytics.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "stats")
public class Statistic {
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private UUID player;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String scope;
    @Getter
    @DatabaseField(defaultValue = "0")
    private int value;

    Statistic() {
        // required for ORMLite
    }

    public Statistic(UUID player, String scope, int value) {
        this.player = player;
        this.scope = scope;
        this.value = value;
    }
}
