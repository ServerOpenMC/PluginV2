package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "company_permissions")
public class CompanyPermission {
    @DatabaseField(canBeNull = false)
    private UUID company;
    @DatabaseField(canBeNull = false)
    private UUID player;
    @DatabaseField(canBeNull = false)
    private String permission;

    CompanyPermission() {
        // required for ORMLite
    }
}
