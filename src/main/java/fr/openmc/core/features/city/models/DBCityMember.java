package fr.openmc.core.features.city.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "city_members")
public class DBCityMember {
    @DatabaseField(id = true)
    @Getter
    private UUID player;
    @DatabaseField(canBeNull = false)
    @Getter
    private String city;

    DBCityMember() {
        // required for ORMLite
    }

    public DBCityMember(UUID player, String city) {
        this.player = player;
        this.city = city;
    }
}
