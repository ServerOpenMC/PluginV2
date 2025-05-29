package fr.openmc.core.features.city.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.city.CPermission;

@DatabaseTable(tableName = "city_permissions")
public class DBCityPermission {
    @DatabaseField(canBeNull = false)
    private String city;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private UUID player;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String permission;

    DBCityPermission() {
        // required for ORMLite
    }

    public CPermission getPermission() {
        return CPermission.valueOf(permission);
    }
}
