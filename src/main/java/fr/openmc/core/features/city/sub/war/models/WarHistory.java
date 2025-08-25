package fr.openmc.core.features.city.sub.war.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "war_history")
@Getter
@Setter
public class WarHistory {
    @DatabaseField(id = true, columnName = "uuid")
    private String cityUUID;
    @DatabaseField
    private int numberWon;
    @DatabaseField
    private int numberWar;

    WarHistory() {
        // required for ORMLite
    }

    public WarHistory(String uuid) {
        this.cityUUID = uuid;
    }

    public void addWin() {
        numberWon += 1;
    }

    public void addParticipation() {
        numberWar += 1;
    }
}