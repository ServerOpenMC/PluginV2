package fr.openmc.core.features.city.sub.war.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "war_history")
@Getter
@Setter
public class WarHistory {
    @DatabaseField(id = true, columnName = "city_uuid")
    private UUID cityUUID;
    @DatabaseField
    private int numberWon;
    @DatabaseField
    private int numberWar;

    WarHistory() {
        // required for ORMLite
    }

    public WarHistory(UUID uuid) {
        this.cityUUID = uuid;
    }

    public void addWin() {
        numberWon += 1;
    }

    public void addParticipation() {
        numberWar += 1;
    }
}