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
    @DatabaseField(canBeNull = false, uniqueCombo = true, columnName = "player")
    @Getter
    private UUID playerUUID;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String permission;

    DBCityPermission() {
        // required for ORMLite
    }

    public DBCityPermission(UUID cityUUID, UUID playerUUID, String permission) {
        this.cityUUID = cityUUID;
        this.playerUUID = playerUUID;
        this.permission = permission;
    }

    public CityPermission getPermission() {
        return CityPermission.valueOf(permission);
    }
}
