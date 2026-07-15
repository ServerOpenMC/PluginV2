package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * {
 *   "clock": "minecraft:overworld",
 *   "period_ticks": 24000,
 *   "time_markers": {
 *     "minecraft:day": {
 *       "show_in_commands": true,
 *       "ticks": 1000
 *     },
 *     "minecraft:midnight": {
 *       "show_in_commands": true,
 *       "ticks": 18000
 *     },
 *     "minecraft:night": {
 *       "show_in_commands": true,
 *       "ticks": 13000
 *     },
 *     "minecraft:noon": {
 *       "show_in_commands": true,
 *       "ticks": 6000
 *     },
 *     "minecraft:roll_village_siege": 18000,
 *     "minecraft:wake_up_from_sleep": 0
 *   },
 *   "tracks": {
 *     "minecraft:visual/sky_color": {
 *       "keyframes": [
 *         {
 *           "ticks": 133,
 *           "value": "#ffffff"
 *         },
 *         {
 *           "ticks": 11867,
 *           "value": "#ffffff"
 *         },
 *         {
 *           "ticks": 13670,
 *           "value": "#000000"
 *         },
 *         {
 *           "ticks": 22330,
 *           "value": "#000000"
 *         }
 *       ],
 *       "modifier": "multiply"
 *     }
 *   }
 * }
 */
public final class TimelineBuilder {
    private String clock = "minecraft:overworld";
    private Integer periodTicks = null;
    private final Map<String, TrackBuilder> tracks = new LinkedHashMap<>();
    private final Map<String, Object> timeMarkers = new LinkedHashMap<>();

    public TimelineBuilder clock(String clock) {
        this.clock = clock;
        return this;
    }

    public TimelineBuilder periodTicks(int periodTicks) {
        this.periodTicks = periodTicks;
        return this;
    }

    public TimelineBuilder track(String attributeId, Consumer<TrackBuilder> builder) {
        TrackBuilder track = new TrackBuilder();
        builder.accept(track);
        tracks.put(attributeId, track);
        return this;
    }

    public TimelineBuilder timeMarker(String id, int ticks) {
        timeMarkers.put(id, ticks);
        return this;
    }

    public TimelineBuilder timeMarker(String id, int ticks, boolean showInCommands) {
        JsonObject marker = new JsonObject();
        marker.addProperty("ticks", ticks);
        marker.addProperty("show_in_commands", showInCommands);
        timeMarkers.put(id, marker);
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("clock", clock);
        if (periodTicks != null) json.addProperty("period_ticks", periodTicks);

        if (!tracks.isEmpty()) {
            JsonObject tracksJson = new JsonObject();
            for (var entry : tracks.entrySet()) {
                tracksJson.add(entry.getKey(), entry.getValue().toJson());
            }
            json.add("tracks", tracksJson);
        }

        if (!timeMarkers.isEmpty()) {
            JsonObject markersJson = new JsonObject();
            for (var entry : timeMarkers.entrySet()) {
                if (entry.getValue() instanceof Integer i) {
                    markersJson.addProperty(entry.getKey(), i);
                } else if (entry.getValue() instanceof JsonObject obj) {
                    markersJson.add(entry.getKey(), obj);
                }
            }
            json.add("time_markers", markersJson);
        }

        return json;
    }

    public final class TrackBuilder {
        private String ease = null;
        private JsonElement easeObject = null;
        private String modifier = null;
        private final List<KeyframeBuilder> keyframes = new ArrayList<>();

        public TrackBuilder ease(String ease) {
            this.ease = ease;
            return this;
        }

        public TrackBuilder easeCubicBezier(double x1, double y1, double x2, double y2) {
            JsonObject obj = new JsonObject();
            JsonArray bezier = new JsonArray();
            bezier.add(x1); bezier.add(y1); bezier.add(x2); bezier.add(y2);
            obj.add("cubic_bezier", bezier);
            this.easeObject = obj;
            return this;
        }

        public TrackBuilder modifier(String modifier) {
            this.modifier = modifier;
            return this;
        }

        public TrackBuilder keyframe(int ticks, String value) {
            keyframes.add(new KeyframeBuilder(ticks, new JsonPrimitive(value)));
            return this;
        }

        public TrackBuilder keyframe(int ticks, double value) {
            keyframes.add(new KeyframeBuilder(ticks, new JsonPrimitive(value)));
            return this;
        }

        public TrackBuilder keyframe(int ticks, boolean value) {
            keyframes.add(new KeyframeBuilder(ticks, new JsonPrimitive(value)));
            return this;
        }

        public TrackBuilder keyframe(int ticks, JsonElement value) {
            keyframes.add(new KeyframeBuilder(ticks, value));
            return this;
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (easeObject != null) {
                json.add("ease", easeObject);
            } else if (ease != null) {
                json.addProperty("ease", ease);
            }
            if (modifier != null) json.addProperty("modifier", modifier);

            JsonArray kfArray = new JsonArray();
            for (KeyframeBuilder kf : keyframes) {
                kfArray.add(kf.toJson());
            }
            json.add("keyframes", kfArray);
            return json;
        }



        private record KeyframeBuilder(int ticks, JsonElement value) {
            private JsonObject toJson() {
                JsonObject json = new JsonObject();
                json.addProperty("ticks", ticks);
                json.add("value", value);
                return json;
            }
        }
    }
}
