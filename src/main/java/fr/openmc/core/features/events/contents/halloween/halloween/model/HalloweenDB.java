package fr.openmc.core.features.events.contents.halloween.halloween.model;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@DatabaseTable(tableName = "halloween_season_event_data")
public class HalloweenDB {

    @Setter
    @DatabaseField(columnName = "advancement")
    private String currentAdvancement;

    @Setter
    @DatabaseField(columnName = "unlocked_room")
    private String unlockedRooms;

    @Setter
    @DatabaseField(columnName = "is_active")
    private boolean active;

    private static final Gson GSON = new Gson();

    public HalloweenDB() {

    }

    public HalloweenDB(String currentAdvancement, String currentUnlocked, boolean active) {
        this.currentAdvancement = currentAdvancement;
        this.unlockedRooms = currentUnlocked;
        this.active = active;
    }

    public void setContent(String[] array) {
        unlockedRooms = GSON.toJson(array);
    }

    public String[] getContent() {
        if (unlockedRooms == null) return new String[0];
        return GSON.fromJson(unlockedRooms, String[].class);
    }

}
