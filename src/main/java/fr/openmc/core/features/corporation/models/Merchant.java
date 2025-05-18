package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "merchants")
public class Merchant {
    @DatabaseField(id = true)
    private UUID id;
    @DatabaseField
    private byte[] content;

    Merchant() {
        // required for ORMLite
    }
}
