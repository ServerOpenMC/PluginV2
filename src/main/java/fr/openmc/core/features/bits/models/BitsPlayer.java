package fr.openmc.core.features.bits.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "bits")
public class BitsPlayer {
    @DatabaseField(id = true, columnName = "player")
    private UUID playerUUID;

    @Setter
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private double bits;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "last_saved_lines")
    private int lastSavedLines;

    BitsPlayer() {
        // necessary for OrmLite
    }

    public BitsPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.bits = 0;
        this.lastSavedLines = 0;
    }

    public void deposit(double amount) {
        bits += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= bits) {
            bits -= amount;
            return true;
        }
        return false;
    }
}

