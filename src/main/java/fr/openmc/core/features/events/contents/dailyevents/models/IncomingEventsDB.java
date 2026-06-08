package fr.openmc.core.features.events.contents.dailyevents.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@DatabaseTable(tableName = "daily_event_incoming")
public class IncomingEventsDB {
    @DatabaseField(id = true, columnName = "id")
    private int id = 1;

    @Setter
    @DatabaseField(columnName = "incomings", dataType = DataType.SERIALIZABLE)
    private String[] dailyEventsIdIncomings;

    public IncomingEventsDB() {}

    public IncomingEventsDB(List<ScheduleDailyEvent> incomingEventsList) {
        this.dailyEventsIdIncomings = incomingEventsList.stream()
                .map(d->d.getDailyEvent().getEventId())
                .toArray(String[]::new);
    }

    public List<DailyEvent> getDailyEventsIncomings() {
        return Arrays.stream(dailyEventsIdIncomings)
                .map(id -> DailyEventsManager.EVENTS.stream()
                        .filter(event -> event.getEventId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Aucun daily event trouvé pour l'id : " + id)))
                .collect(toList());
    }
}
