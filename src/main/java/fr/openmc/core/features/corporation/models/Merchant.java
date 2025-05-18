package fr.openmc.core.features.corporation.models;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "merchants")
public class Merchant {
    @DatabaseField(id = true)
    private UUID id;
    @Getter
    @DatabaseField
    private byte[] content;

    Merchant() {
        // required for ORMLite
    }

    public Merchant(UUID id, byte[] content) {
        this.id = id;
        this.content = content;
    }
}
