package fr.openmc.api.datapacks.injectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.openmc.api.datapacks.DatapackInjector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe qui représente les données trouvable dans un dimension type
 * Qui injecte directement cela sous forme .json dans le datapack
 */
public class DimensionTypesInjector implements DatapackInjector {

    private final String namespace;
    private final Map<String, DimensionTypeBuilder> entries = new LinkedHashMap<>();

    public DimensionTypesInjector(String namespace) {
        this.namespace = namespace;
    }

    public DimensionTypesInjector add(String id, Consumer<DimensionTypeBuilder> builder) {
        DimensionTypeBuilder instance = new DimensionTypeBuilder();
        builder.accept(instance);
        entries.put(id, instance);
        return this;
    }

    public DimensionTypesInjector add(String id, DimensionTypeBuilder builder) {
        entries.put(id, builder);
        return this;
    }

    @Override
    public void inject(File rootFile) {
        if (entries.isEmpty()) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("dimension_type");
        try {
            Files.createDirectories(root);
            for (var entry : entries.entrySet()) {
                Path dimensionTypeFile = root.resolve(entry.getKey() + ".json");
                Files.writeString(dimensionTypeFile, GSON.toJson(entry.getValue().toJson()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write dimension_type files", e);
        }
    }

    /**
     * Exemple simple d'un dimension type :
     * {
     *   "attributes": {},
     *   "ambient_light": 0,
     *   "coordinate_scale": 1,
     *   "default_clock": "minecraft:overworld",
     *   "has_ceiling": false,
     *   "has_ender_dragon_fight": false,
     *   "has_skylight": true,
     *   "height": 384,
     *   "infiniburn": "#minecraft:infiniburn_overworld",
     *   "logical_height": 384,
     *   "min_y": -64,
     *   "monster_spawn_block_light_limit": 0,
     *   "monster_spawn_light_level": {
     *     "type": "minecraft:uniform",
     *     "max_inclusive": 7,
     *     "min_inclusive": 0
     *   },
     *   "timelines": "#minecraft:in_overworld"
     * }
     */
    public static final class DimensionTypeBuilder {
        private JsonObject attributes;
        private Double ambientLight = 0.0;
        private Double coordinateScale = 1.0;
        private String defaultClock = "overworld";
        private Boolean hasCeiling = false;
        private Boolean hasEnderDragonFlight = false;
        private Boolean hasSkylight = true;
        private Integer height = 384;
        private String infiniburn = "#infiniburn_overworld";
        private Integer logicalHeight = 384;
        private Integer minY = -64;
        private Integer monsterSpawnBlockLightLimit = 0;
        private JsonElement monsterSpawnLightLevel = new JsonPrimitive(0);
        private String timelines;

        public DimensionTypeBuilder attributes(Consumer<JsonObject> builder) {
            JsonObject obj = new JsonObject();
            builder.accept(obj);
            this.attributes = obj;
            return this;
        }

        public DimensionTypeBuilder attributes(JsonObject attributes) {
            this.attributes = attributes;
            return this;
        }

        public DimensionTypeBuilder ambientLight(double ambientLight) {
            this.ambientLight = ambientLight;
            return this;
        }

        public DimensionTypeBuilder coordinateScale(double coordinateScale) {
            this.coordinateScale = coordinateScale;
            return this;
        }

        public DimensionTypeBuilder defaultClock(String keyOfClock) {
            this.defaultClock = keyOfClock;
            return this;
        }

        public DimensionTypeBuilder hasCeiling(boolean hasCeiling) {
            this.hasCeiling = hasCeiling;
            return this;
        }

        public DimensionTypeBuilder hasEnderDragonFlight(boolean hasEnderDragonFlight) {
            this.hasEnderDragonFlight = hasEnderDragonFlight;
            return this;
        }

        public DimensionTypeBuilder hasSkylight(boolean hasSkylight) {
            this.hasSkylight = hasSkylight;
            return this;
        }

        public DimensionTypeBuilder height(int height) {
            this.height = height;
            return this;
        }

        public DimensionTypeBuilder infiniburn(String infiniburn) {
            this.infiniburn = infiniburn;
            return this;
        }

        public DimensionTypeBuilder logicalHeight(int logicalHeight) {
            this.logicalHeight = logicalHeight;
            return this;
        }

        public DimensionTypeBuilder minY(int minY) {
            this.minY = minY;
            return this;
        }

        public DimensionTypeBuilder monsterSpawnBlockLightLimit(int limit) {
            this.monsterSpawnBlockLightLimit = limit;
            return this;
        }

        public DimensionTypeBuilder monsterSpawnLightLevelUniform(int minInclusive, int maxInclusive) {
            JsonObject uniform = new JsonObject();
            uniform.addProperty("type", "minecraft:uniform");
            uniform.addProperty("min_inclusive", minInclusive);
            uniform.addProperty("max_inclusive", maxInclusive);
            this.monsterSpawnLightLevel = uniform;
            return this;
        }

        public DimensionTypeBuilder monsterSpawnLightLevel(int level) {
            this.monsterSpawnLightLevel = new JsonPrimitive(level);
            return this;
        }

        public DimensionTypeBuilder monsterSpawnLightLevel(JsonElement monsterSpawnLightLevel) {
            this.monsterSpawnLightLevel = monsterSpawnLightLevel;
            return this;
        }

        public DimensionTypeBuilder timelines(String timelines) {
            this.timelines = timelines;
            return this;
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (attributes != null) json.add("attributes", attributes);
            if (ambientLight != null) json.addProperty("ambient_light", ambientLight);
            if (coordinateScale != null) json.addProperty("coordinate_scale", coordinateScale);
            if (defaultClock != null) json.addProperty("default_clock", defaultClock);
            if (hasCeiling != null) json.addProperty("has_ceiling", hasCeiling);
            if (hasEnderDragonFlight != null) json.addProperty("has_ender_dragon_fight", hasEnderDragonFlight);
            if (hasSkylight != null) json.addProperty("has_skylight", hasSkylight);
            if (height != null) json.addProperty("height", height);
            if (infiniburn != null) json.addProperty("infiniburn", infiniburn);
            if (logicalHeight != null) json.addProperty("logical_height", logicalHeight);
            if (minY != null) json.addProperty("min_y", minY);
            if (monsterSpawnBlockLightLimit != null) json.addProperty("monster_spawn_block_light_limit", monsterSpawnBlockLightLimit);
            if (monsterSpawnLightLevel != null) json.add("monster_spawn_light_level", monsterSpawnLightLevel);
            if (timelines != null) json.addProperty("timelines", timelines);
            return json;
        }
    }
}
