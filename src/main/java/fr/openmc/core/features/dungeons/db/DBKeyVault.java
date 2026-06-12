package fr.openmc.core.features.dungeons.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.jna.WString;
import fr.openmc.core.features.dungeons.Rarity;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "db_key_vault")
@Getter
public class DBKeyVault {
    @DatabaseField(columnName = "player_uuid")
    private UUID playerUUID;

    @DatabaseField(columnName = "key_level")
    private int keyLevel;

    @DatabaseField(columnName = "rarity_key")
    private String rarityKeyStr;

    @DatabaseField(columnName = "key_number")
    private int keyNumber;

    private Rarity rarity;

    DBKeyVault() {

    }

    public DBKeyVault(UUID playerUUID, int keyLevel, int keyNumber, Rarity rarity) {
        this.playerUUID = playerUUID;
        this.keyLevel = keyLevel;
        this.keyNumber = keyNumber;
        this.rarity = rarity;
    }

    public DBKeyVault serialize() {
        this.rarityKeyStr = rarity.name();
        return this;
    }

}
