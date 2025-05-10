package fr.openmc.core.features.analytics;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "stats")
class Statistic {
    Statistic() {
        // required for ORMLite
    }

    Statistic(String player, String scope, int value) {
        this.player = player;
        this.scope = scope;
        this.value = value;
    }

    @DatabaseField(canBeNull = false)
    private String player;
    @DatabaseField(canBeNull = false)
    private String scope;
    @Getter
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int value;
}
