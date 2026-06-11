package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

/**
 * Exemple simple d'un biome:
 * {
 *   "attributes": {},
 *   "carvers": [],
 *   "creature_spawn_probability": 0.03,
 *   "downfall": 0,
 *   "effects": {
 *     "foliage_color": "#9e814d",
 *     "grass_color": "#90814d",
 *     "water_color": "#3f76e4",
 *     "dry_foliage_color": "",
 *     "grass_color_modifier": "none"
 *   },
 *   "features": [],
 *   "has_precipitation": false,
 *   "spawn_costs": {},
 *   "spawners": {},
 *   "temperature": 2
 * }
 */
public final class BiomeBuilder {
    private JsonObject attributes = new JsonObject();
    private final JsonArray carvers = new JsonArray();
    private final JsonObject effects = new JsonObject();
    {
        effects.addProperty("water_color", "#3f76e4");
    }
    private final JsonArray features = new JsonArray();
    private final JsonObject spawnCosts = new JsonObject();
    private final JsonObject spawners = new JsonObject();
    private String temperatureModifier = "none";
    private Double creatureSpawnProbability = 0.03;
    private Double downfall = 0.5;
    private Double temperatures = 0.5;
    private Boolean hasPrecipitation = true;

    public BiomeBuilder attributes(EnvironnementAttributeBuilder builder) {
        this.attributes = builder.getOutputData();
        return this;
    }

    public BiomeBuilder carver(String id) {
        this.carvers.add(id);
        return this;
    }

    public BiomeBuilder features(JsonElement id) {
        this.features.add(id);
        return this;
    }

    public BiomeBuilder temperatureModifier(String id) {
        this.temperatureModifier =id;
        return this;
    }

    public BiomeBuilder creatureSpawnProbability(Double value) {
        this.creatureSpawnProbability=value;
        return this;
    }

    public BiomeBuilder downfall(Double value) {
        this.downfall=value;
        return this;
    }

    public BiomeBuilder effects(Consumer<JsonObject> builder) {
        JsonObject obj = new JsonObject();
        builder.accept(obj);
        for (var entry : obj.entrySet()) {
            this.effects.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BiomeBuilder waterColor(String color) {
        this.effects.addProperty("water_color", color);
        return this;
    }

    public BiomeBuilder grassColor(String color) {
        this.effects.addProperty("grass_color", color);
        return this;
    }

    public BiomeBuilder foliageColor(String color) {
        this.effects.addProperty("foliage_color", color);
        return this;
    }

    public BiomeBuilder dryFoliageColor(String color) {
        this.effects.addProperty("dry_foliage_color", color);
        return this;
    }

    /**
     * Set la grass color modifier
     * @param id none, dark_forest, swamp
     * @return le builder
     */
    public BiomeBuilder grassColorModifier(String id) {
        this.effects.addProperty("grass_color_modifier", id);
        return this;
    }

    public BiomeBuilder spawnCosts(Consumer<JsonObject> builder) {
        JsonObject obj = new JsonObject();
        builder.accept(obj);
        for (var entry : obj.entrySet()) {
            this.spawnCosts.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BiomeBuilder spawnCosts(JsonObject spawnCosts) {
        for (var entry : spawnCosts.entrySet()) {
            this.spawnCosts.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BiomeBuilder spawners(JsonObject spawners) {
        for (var entry : spawners.entrySet()) {
            this.spawners.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BiomeBuilder temperatures(Double value) {
        this.temperatures=value;
        return this;
    }

    public BiomeBuilder temperatures(Boolean bool) {
        this.hasPrecipitation=bool;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (attributes != null) json.add("attributes", attributes);
        if (temperatureModifier != null) json.addProperty("temperature_modifier", temperatureModifier);
        if (creatureSpawnProbability != null) json.addProperty("creature_spawn_probability", creatureSpawnProbability);
        if (carvers != null) json.add("carvers", carvers);
        if (downfall != null) json.addProperty("downfall", downfall);
        if (effects != null) json.add("effects", effects);
        if (features != null) json.add("features", features);
        if (hasPrecipitation != null) json.addProperty("has_precipitation", hasPrecipitation);
        if (spawnCosts != null) json.add("spawn_costs", spawnCosts);
        if (spawners != null) json.add("spawners", spawners);
        if (temperatures != null) json.addProperty("temperature", temperatures);

        return json;
    }
}
