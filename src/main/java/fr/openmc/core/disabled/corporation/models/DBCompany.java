package fr.openmc.core.disabled.corporation.models;

import fr.openmc.core.features.city.City;
import fr.openmc.core.disabled.corporation.company.Company;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "companies")
public class DBCompany {
    @DatabaseField(id = true)
    private UUID id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private UUID owner;
    @DatabaseField
    private String city;
    @DatabaseField(canBeNull = false)
    private double cut;
    @DatabaseField(canBeNull = false)
    private double balance;

    DBCompany() {
        // required for ORMLite
    }

    public DBCompany(UUID id, String name, UUID owner, City city, double cut, double balance) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.city = city == null ? null : city.getUUID();
        this.cut = cut;
        this.balance = balance;
    }

    public Company deserialize() {
        return new Company(id, name, owner, city, cut, balance);
    }
}
