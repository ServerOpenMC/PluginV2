package fr.openmc.core.features.city.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "city_members")
public class DBCityMember {
    @DatabaseField(id = true)
    private UUID player;
    @DatabaseField(canBeNull = false)
    private String city;

    DBCityMember() {
        // required for ORMLite
    }
}
