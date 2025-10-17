package fr.openmc.core.features.dream.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "dream_player")
@Getter
public class DBDreamPlayer {
    @DatabaseField(id = true, columnName = "uuid")
    private UUID playerUUID;

    @DatabaseField(canBeNull = false, columnName = "max_dream_time")
    private Long maxDreamTime;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "dream_inventory", dataType = DataType.LONG_STRING)
    private String dreamInventory;

    @DatabaseField(columnName = "dream_x")
    private Double dreamX;
    @DatabaseField(columnName = "dream_y")
    private Double dreamY;
    @DatabaseField(columnName = "dream_z")
    private Double dreamZ;

    @Setter
    @DatabaseField(columnName = "progression_orb")
    private int progressionOrb;

    DBDreamPlayer() {
        // Default constructor for ORMLite
    }

    public DBDreamPlayer(UUID playerUUID, Long maxDreamTime, String serializedDreamInv) {
        this.playerUUID = playerUUID;
        this.maxDreamTime = maxDreamTime;

        this.dreamInventory = serializedDreamInv;

        this.progressionOrb = 0;
    }

    public DBDreamPlayer(UUID playerUUID, Long maxDreamTime, String serializedDreamInv, double dreamX, double dreamY, double dreamZ) {
        this.playerUUID = playerUUID;
        this.maxDreamTime = maxDreamTime;

        this.dreamInventory = serializedDreamInv;

        this.dreamX = dreamX;
        this.dreamY = dreamY;
        this.dreamZ = dreamZ;
    }
}
