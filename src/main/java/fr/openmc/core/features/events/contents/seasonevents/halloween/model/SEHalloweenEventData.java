package fr.openmc.core.features.events.contents.seasonevents.halloween.model;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import lombok.Getter;
import lombok.Setter;

@Getter
@DatabaseTable(tableName = "halloween_season_event_data")
public class SEHalloweenEventData {

    @DatabaseField(id = true, columnName = "id")
    private int id = 1;

    @Setter
    @DatabaseField(columnName = "advancement")
    private String currentAdvencement;

    @Setter
    @DatabaseField(columnName = "unlocked_room")
    private String currentUnlocked;

    @Setter
    @DatabaseField(columnName = "is_active")
    private boolean active;

    public SEHalloweenEventData() {}

    public SEHalloweenEventData(WeeklyEvent currentEvent, WeeklyEventPhase currentPhase) {
//        this.currentEvent = currentEvent.getId();
//        if (currentPhase == null)
//            this.currentPhase = null;
//        else
//            this.currentPhase = currentPhase.getId();
    }

    private static final Gson GSON = new Gson();

    public void setContent(String[] array) {
        currentUnlocked = GSON.toJson(array);
    }

    public String[] getContent() {
        if (currentUnlocked == null) return new String[0];
        return GSON.fromJson(currentUnlocked, String[].class);
    }

}
