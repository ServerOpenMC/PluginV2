package fr.openmc.core.features.corporation.models;

import fr.openmc.core.features.corporation.company.Company;

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
    private UUID city;
    @DatabaseField(canBeNull = false)
    private double cut;
    @DatabaseField(canBeNull = false)
    private double balance;

    DBCompany() {
        // required for ORMLite
    }

    public Company deserialize() {
        return new Company(id, name, owner, city, cut, balance);
    }
}
