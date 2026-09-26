package fr.openmc.core.features.events.contents.halloween.halloween.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@DatabaseTable(tableName = "halloween_season_event_data")
public class HalloweenDB {

    @DatabaseField(columnName = "advancement")
    private int currentAdvancement;

    @DatabaseField(columnName = "unlocked_room")
    private String unlockedRooms;

    @DatabaseField(columnName = "is_active")
    private boolean active;

    private static final Gson GSON = new Gson();

    public HalloweenDB() {
    }

    public HalloweenDB(int currentAdvancement, List<String> unlockedRooms, boolean active) {
        this.currentAdvancement = currentAdvancement;
        this.unlockedRooms = GSON.toJson(unlockedRooms);
        this.active = active;
    }

    public void setUnlockedRooms(List<String> unlockedRooms) {
        this.unlockedRooms = GSON.toJson(unlockedRooms);
    }

    public List<String> getUnlockedRooms() {
        if (unlockedRooms == null || unlockedRooms.isBlank()) {
            return new ArrayList<>();
        }

        return GSON.fromJson(
                unlockedRooms,
                new TypeToken<List<String>>() {}.getType()
        );
    }
}
