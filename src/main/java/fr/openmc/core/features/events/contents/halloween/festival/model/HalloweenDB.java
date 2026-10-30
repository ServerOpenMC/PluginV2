package fr.openmc.core.features.events.contents.halloween.festival.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.*;

@Getter
@Setter
@DatabaseTable(tableName = "halloween_festival_event")
public class HalloweenDB {

    private static final Gson GSON = new Gson();

    private static final Type UUID_SET_TYPE =
            new TypeToken<Set<UUID>>() {}.getType();

    @DatabaseField(columnName = "global_advancement")
    private int globalAdvancement;

    @DatabaseField(columnName = "floor_advancement")
    private int floorAdvancement;

    @DatabaseField(columnName = "is_active")
    private boolean active;

    @DatabaseField(generatedId = true)
    private int id;

    @Getter(lombok.AccessLevel.NONE)
    @Setter(lombok.AccessLevel.NONE)
    @DatabaseField(columnName = "participants")
    private String participantsJson = "[]";

    public HalloweenDB() {
    }

    public HalloweenDB(int globalAdvancement, int floorAdvancement, boolean active) {
        this.globalAdvancement = globalAdvancement;
        this.floorAdvancement = floorAdvancement;
        this.active = active;
    }

    public Set<UUID> getParticipants() {
        Set<UUID> participants = GSON.fromJson(participantsJson, UUID_SET_TYPE);
        return participants != null ? participants : new HashSet<>();
    }

    public void setParticipants(Set<UUID> participants) {
        this.participantsJson = GSON.toJson(participants);
    }
}