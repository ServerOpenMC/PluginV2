package fr.openmc.api.datapacks.builders.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/** Environnement Attribute de son
 * "minecraft:audio/ambient_sounds": {
 *       "loop": "minecraft:ambient.crimson_forest.loop",
 *       "mood": {
 *         "sound": "minecraft:ambient.basalt_deltas.additions",
 *         "tick_delay": 0,
 *         "block_search_extent": 0,
 *         "offset": 0
 *       },
 *       "additions": [
 *         {
 *           "sound": "minecraft:ambient.basalt_deltas.loop",
 *           "tick_chance": 0
 *         }
 *       ]
 *     }
 */
public class AmbientSoundBuilder {
    private JsonElement loop;
    private JsonObject mood;
    private JsonObject additions;

    public AmbientSoundBuilder mood(MoodBuilder moodBuilder) {
        this.mood = moodBuilder.toJson();
        return this;
    }

    public AmbientSoundBuilder additions(AdditionsBuilder additionsBuilder) {
        this.additions = additionsBuilder.toJson();
        return this;
    }

    public AmbientSoundBuilder loop(String soundId) {
        this.loop = new JsonPrimitive(soundId);
        return this;
    }

    public AmbientSoundBuilder loop(SoundEventBuilder soundEventBuilder) {
        this.loop = soundEventBuilder.toJson();
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (mood != null) json.add("mood", mood);
        if (additions != null) json.add("additions", additions);
        if (loop != null) json.add("loop", loop);
        return json;
    }

    public static class MoodBuilder {
        private Integer blockSearchExtent;
        private Integer offset;
        private JsonElement sound;
        private Integer tickDelay;

        public MoodBuilder blockSearchExtent(int value) {
            this.blockSearchExtent = value;
            return this;
        }

        public MoodBuilder offset(int value) {
            this.offset = value;
            return this;
        }

        public MoodBuilder tickDelay(int value) {
            this.tickDelay = value;
            return this;
        }

        public MoodBuilder sound(String soundId) {
            this.sound = new JsonPrimitive(soundId);
            return this;
        }

        public MoodBuilder sound(SoundEventBuilder soundEventBuilder) {
            this.sound = soundEventBuilder.toJson();
            return this;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (blockSearchExtent != null) json.addProperty("block_search_extent", blockSearchExtent);
            if (offset != null) json.addProperty("offset", offset);
            if (sound != null) json.add("sound", sound);
            if (tickDelay != null) json.addProperty("tick_delay", tickDelay);
            return json;
        }
    }

    public static class AdditionsBuilder {
        private JsonElement sound;
        private Double tickChance;

        public AdditionsBuilder sound(String soundId) {
            this.sound = new JsonPrimitive(soundId);
            return this;
        }

        public AdditionsBuilder sound(SoundEventBuilder soundEventBuilder) {
            this.sound = soundEventBuilder.toJson();
            return this;
        }

        public AdditionsBuilder tickChance(double value) {
            this.tickChance = value;
            return this;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (sound != null) json.add("sound", sound);
            if (tickChance != null) json.addProperty("tick_chance", tickChance);
            return json;
        }
    }
}
