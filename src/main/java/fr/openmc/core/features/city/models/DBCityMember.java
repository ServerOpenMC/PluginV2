package fr.openmc.core.features.city.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "city_members")
public class DBCityMember {
    @DatabaseField(columnName = "player_uuid", id = true)
    @Getter
    private UUID playerUUID;
    @DatabaseField(columnName = "city_uuid", canBeNull = false)
    @Getter
    private UUID cityUUID;

    DBCityMember() {
        // required for ORMLite
    }

    public DBCityMember(UUID player, UUID city) {
        this.playerUUID = player;
        this.cityUUID = city;
    }
}
