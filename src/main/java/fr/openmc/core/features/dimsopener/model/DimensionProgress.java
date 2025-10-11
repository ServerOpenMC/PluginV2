package fr.openmc.core.features.dimsopener.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.dimsopener.DimensionStep;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "dimension_progress")
public class DimensionProgress {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, index = true, columnName = "dimension_key")
    private String dimensionKey;

    @DatabaseField(canBeNull = false, columnName = "current_step")
    private int currentStep;

    @DatabaseField(canBeNull = false, columnName = "items_collected")
    private int itemsCollected;

    @DatabaseField(canBeNull = false, columnName = "next_step_unlock_time")
    private long nextStepUnlockTime;

    @DatabaseField(canBeNull = false, columnName = "unlocked")
    private boolean isUnlocked;

    public DimensionProgress() {
        // Default constructor for ORMLite
    }

    public DimensionProgress(String dimensionKey) {
        this.dimensionKey = dimensionKey;
        this.currentStep = 0;
        this.itemsCollected = 0;
        this.nextStepUnlockTime = 0;
        this.isUnlocked = false;
    }

    public boolean canProgressToNextStep() {
        return System.currentTimeMillis() >= nextStepUnlockTime;
    }
}
