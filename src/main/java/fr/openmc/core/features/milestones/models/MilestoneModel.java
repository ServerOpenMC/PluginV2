package fr.openmc.core.features.milestones.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@DatabaseTable(tableName = "milestone")
@Getter
public class MilestoneModel {
	@DatabaseField(generatedId = true)
	private int milestoneID;
    @DatabaseField(uniqueCombo = true, columnName = "player_uuid")
    private UUID playerUUID;
    @DatabaseField(uniqueCombo = true)
    private String type;
    @DatabaseField(canBeNull = false)
    @Setter
    private int step;
	@DatabaseField(canBeNull = false)
	@Setter
	private int progress;

    MilestoneModel() {
        // required for ORMLite
    }

    public MilestoneModel(UUID playerUUID, MilestoneType type, int step, int progress) {
        this.playerUUID = playerUUID;
        this.type = type.name();
        this.step = step;
		this.progress = progress;
    }
	
	public void incrementProgress() {
		this.progress++;
	}
	
	public void incrementProgress(int progress) {
		this.progress += progress;
	}
}
