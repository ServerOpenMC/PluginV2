package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.CPermission;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "city_permissions")
public class DBCityPermission {
    @DatabaseField(canBeNull = false)
    private String city; //TODO change to cityUUID and city_uuid for db
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    @Getter
    private UUID player;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String permission;

    DBCityPermission() {
        // required for ORMLite
    }

    public DBCityPermission(String city, UUID player, String permission) {
        this.city = city;
        this.player = player;
        this.permission = permission;
    }

    public CPermission getPermission() {
        return CPermission.valueOf(permission);
    }
}
