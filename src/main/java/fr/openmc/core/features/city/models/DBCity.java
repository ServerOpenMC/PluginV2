package fr.openmc.core.features.city.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.city.City;

@DatabaseTable(tableName = "cities")
public class DBCity {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField(canBeNull = false)
    private UUID owner;
    @DatabaseField
    private String name;
    @DatabaseField(defaultValue = "0")
    private double balance;
    @DatabaseField(canBeNull = false)
    private String type;
    @DatabaseField(canBeNull = false)
    private int power;
    @DatabaseField(canBeNull = false, columnName = "free_claims")
    private int freeClaims;

    DBCity() {
        // required for ORMLite
    }

    public City deserialize() {
        return null;
    }
}
