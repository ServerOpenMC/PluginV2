package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.CityPermission;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "city_permissions")
public class DBCityPermission {
    @DatabaseField(columnName = "city_uuid", canBeNull = false)
    private UUID cityUUID;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    @Getter
    private UUID player;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String permission;

    DBCityPermission() {
        // required for ORMLite
    }

    public DBCityPermission(UUID cityUUID, UUID player, String permission) {
        this.cityUUID = cityUUID;
        this.player = player;
        this.permission = permission;
    }

    public CityPermission getPermission() {
        return CityPermission.valueOf(permission);
    }
}
