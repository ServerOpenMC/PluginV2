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
    @DatabaseField(columnName = "current_event")
    private String currentEvent;

    @Setter
    @DatabaseField(columnName = "current_phase")
    private String currentPhase;

    @Setter
    @DatabaseField(columnName = "is_active")
    private boolean active;

    public WeeklyEventsData() {}

    public WeeklyEventsData(WeeklyEvent currentEvent, WeeklyEventPhase currentPhase) {
        this.currentEvent = currentEvent.getId();
        this.currentPhase = currentPhase.getId();
    }
}
