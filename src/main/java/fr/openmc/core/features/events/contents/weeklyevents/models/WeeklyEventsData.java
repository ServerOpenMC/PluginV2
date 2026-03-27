package fr.openmc.core.features.events.contents.weeklyevents.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@DatabaseTable(tableName = "weekly_event_data")
public class WeeklyEventsData {

    @DatabaseField(id = true, columnName = "id")
    private int id = 1;

    @Setter
    @DatabaseField(columnName = "current_event_index")
    private int currentEventIndex;

    @Setter
    @DatabaseField(columnName = "current_phase_index")
    private int currentPhaseIndex;

    @Setter
    @DatabaseField(columnName = "is_active")
    private boolean active;

    public WeeklyEventsData() {}

    public WeeklyEventsData(int currentEventIndex, int currentPhaseIndex) {
        this.currentEventIndex = currentEventIndex;
        this.currentPhaseIndex = currentPhaseIndex;
    }
}
