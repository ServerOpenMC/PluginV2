package fr.openmc.core.features.mailboxes.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mail")
class Mail {
    @DatabaseField(id = true, generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String sender;
    @DatabaseField(canBeNull = false)
    private String receiver;
    // "items" BLOB NOT NULL
    // @DatabaseField(canBeNull = false)
    // pivate Blob items;

    Mail() {
        // required by ORMLite
    }
}
