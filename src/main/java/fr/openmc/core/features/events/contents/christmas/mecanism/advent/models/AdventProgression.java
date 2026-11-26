package fr.openmc.core.features.events.contents.christmas.mecanism.advent.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@DatabaseTable(tableName = "christmas_advent")
public class AdventProgression {

    @DatabaseField(canBeNull = false, columnName = "player_uuid")
    private UUID playerUUID;
    @Setter
    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    private int[] claimed;

    AdventProgression() {
        // required for ORMLite
    }

    public AdventProgression(UUID playerUUID, int[] claimed) {
        this.playerUUID = playerUUID;
        this.claimed = claimed;
    }
}
