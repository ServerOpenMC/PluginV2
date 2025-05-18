package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@Getter
@DatabaseTable(tableName = "shops")
public class DBShop {
    @DatabaseField(id = true)
    private UUID id;

    @DatabaseField
    private UUID owner;
    @DatabaseField
    private UUID city;
    @DatabaseField
    private UUID company;

    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int y;
    @DatabaseField(canBeNull = false)
    private int z;

    DBShop() {
        // required for ORMLite
    }
}
