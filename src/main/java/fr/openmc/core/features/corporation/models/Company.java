package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "companies")
public class Company {
    @DatabaseField(id = true)
    private UUID id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private UUID owner;
    @DatabaseField(canBeNull = false)
    private int cut;
    @DatabaseField(canBeNull = false)
    private double balance;
    @DatabaseField
    private UUID city;

    Company() {
        // required for ORMLite
    }
}
