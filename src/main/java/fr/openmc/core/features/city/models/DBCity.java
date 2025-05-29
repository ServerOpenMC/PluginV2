package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.openmc.core.features.city.City;
import lombok.Getter;

@DatabaseTable(tableName = "cities")
public class DBCity {
    @DatabaseField(id = true)
    @Getter
    private String id;
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

    public DBCity(String id, String name, double balance, String type, int power, int freeClaims) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.type = type;
        this.power = power;
        this.freeClaims = freeClaims;
    }

    public City deserialize() {
        return new City(id, name, balance, type, power, freeClaims);
    }
}
