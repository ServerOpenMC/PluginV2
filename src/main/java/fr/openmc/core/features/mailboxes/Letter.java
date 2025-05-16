package fr.openmc.core.features.mailboxes;

import java.sql.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;

@DatabaseTable(tableName = "mail")
class Letter {
    @DatabaseField(id = true, generatedId = true)
    @Getter
    private int id;
    @DatabaseField(canBeNull = false)
    private String sender;
    @DatabaseField(canBeNull = false)
    private String receiver;
    @DatabaseField(canBeNull = false)
    private byte[] items;
    @DatabaseField(columnName = "num_items", canBeNull = false)
    private int numItems;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date sent;
    @DatabaseField
    private boolean refused;

    Letter() {
        // required by ORMLite
    }

    Letter(String sender, String receiver, byte[] items, int numItems, boolean refused) {
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.numItems = numItems;
        this.refused = refused;
    }

    Letter(int id, String sender, String receiver, byte[] items, int numItems, Date sent, boolean refused) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.numItems = numItems;
        this.sent = sent;
        this.refused = refused;
    }

    public boolean isRefused() {
        return refused;
    }
}
