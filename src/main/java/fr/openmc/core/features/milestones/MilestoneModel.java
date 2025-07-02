package fr.openmc.core.features.milestones;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "milestone")
@Getter
public class MilestoneModel {
    @DatabaseField(id = true, columnName = "uuid")
    private java.util.UUID UUID;
    @DatabaseField
    private String type;
    @DatabaseField(canBeNull = false)
    @Setter
    private int step;

    MilestoneModel() {
        // required for ORMLite
    }

    public MilestoneModel(java.util.UUID uuid, MilestoneType type, int step) {
        this.UUID = uuid;
        this.type = type.name();
        this.step = step;
    }
}
