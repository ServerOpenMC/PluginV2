package fr.openmc.api.datapacks.builders.sounds;

import com.google.gson.JsonObject;

/**
 * {
 *  "sound_id": "minecraft:music.game",
 *  "range": 1
 * }
 */
public class SoundEventBuilder {
    private String soundId;
    private Integer range;

    public SoundEventBuilder soundId(String id) {
        this.soundId = id;
        return this;
    }

    public SoundEventBuilder range(int range) {
        this.range = range;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (soundId != null) json.addProperty("sound_id", soundId);
        if (range != null) json.addProperty("range", range);
        return json;
    }
}
