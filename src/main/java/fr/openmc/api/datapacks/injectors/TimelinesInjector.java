package fr.openmc.api.datapacks.injectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.openmc.api.datapacks.DatapackInjector;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
/**
 * Classe qui représente les données trouvable dans une timeline
 * Qui injecte directement cela sous forme .json dans le datapack
 * https://minecraft.wiki/w/Timeline
 */
public class TimelinesInjector implements DatapackInjector {

    private final String namespace;
    private String id;
    private final Map<String, TimelineBuilder> entries = new LinkedHashMap<>();

    public TimelinesInjector(String namespace) {
        this.namespace = namespace;
    }

    public TimelinesInjector add(String id, Consumer<TimelineBuilder> builder) {
        TimelineBuilder instance = new TimelineBuilder();
        builder.accept(instance);
        entries.put(id, instance);
        this.id = id;
        return this;
    }

    public TimelinesInjector add(String id, TimelineBuilder builder) {
        entries.put(id, builder);
        this.id = id;
        return this;
    }

    @Override
    public void inject(File rootFile) {
        if (entries.isEmpty()) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("timeline");
        try {
            Files.createDirectories(root);
            for (var entry : entries.entrySet()) {
                Path file = root.resolve(entry.getKey() + ".json");
                Files.writeString(file, GSON.toJson(entry.getValue().toJson()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write timeline files", e);
        }
    }

    public static final class TimelineBuilder {
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

        private JsonObject toJson() {
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
    }

    public static final class TrackBuilder {
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
