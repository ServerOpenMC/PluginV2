package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Particle;

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
    private final JsonObject attributes = new JsonObject();
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

    // todo: EnvironnemenAttributeBuilder
    public BiomeBuilder attributes(Consumer<JsonObject> builder) {
        JsonObject obj = new JsonObject();
        builder.accept(obj);
        for (var entry : obj.entrySet()) {
            this.attributes.add(entry.getKey(), toOverridenEnvironnementAttribute(entry.getValue()));
        }
        return this;
    }

    public BiomeBuilder attributes(JsonObject attributes) {
        for (var entry : attributes.entrySet()) {
            this.attributes.add(entry.getKey(), toOverridenEnvironnementAttribute(entry.getValue()));
        }
        return this;
    }

    /**
     * Ajoute un attribut "minecraft:visual/ambient_particles" simple.
     * Exemple :
     * "minecraft:visual/ambient_particles": [ { "particle": { "type": "minecraft:crimson_spore" }, "probability": 0.025 } ]
     */
    public BiomeBuilder ambientParticles(String particleType, double probability) {
        JsonObject entry = new JsonObject();
        JsonObject particle = new JsonObject();
        particle.addProperty("type", particleType);
        entry.add("particle", particle);
        entry.addProperty("probability", probability);

        String key = "minecraft:visual/ambient_particles";
        if (this.attributes.has(key)) {
            this.attributes.getAsJsonObject(key)
                    .getAsJsonArray("argument")
                    .add(entry);
        } else {
            JsonArray arr = new JsonArray();
            arr.add(entry);
            this.attributes.add(key, toOverridenEnvironnementAttribute(arr));
        }
        return this;
    }

    /**
     * {
     *  "particle": {
     *    "type": "minecraft:dust_color_transition",
     *    "from_color": 16776172,
     *    "to_color": 16766720,
     *    "scale": 1
     *   },
     *   "probability": 0.1
     * }
     */
    public BiomeBuilder particleDustColorTransition(int fromColor, int toColor, float scale, double probability) {
        JsonObject entry = new JsonObject();
        JsonObject particle = new JsonObject();
        particle.addProperty("type", "minecraft:dust_color_transition");
        particle.addProperty("from_color", fromColor);
        particle.addProperty("to_color", toColor);
        particle.addProperty("scale", scale);
        entry.add("particle", particle);
        entry.addProperty("probability", probability);

        String key = "minecraft:visual/ambient_particles";
        if (this.attributes.has(key)) {
            this.attributes.getAsJsonObject(key)
                    .getAsJsonArray("argument")
                    .add(entry);
        } else {
            JsonArray arr = new JsonArray();
            arr.add(entry);
            this.attributes.add(key, toOverridenEnvironnementAttribute(arr));
        }
        return this;
    }

    /**
     * Ajoute un attribut "minecraft:visual/ambient_particles" simple.
     * Exemple :
     * "minecraft:visual/ambient_particles": [ { "particle": { "type": "minecraft:crimson_spore" }, "probability": 0.025 } ]
     */
    public BiomeBuilder ambientParticles(Particle particle, double probability) {
        return ambientParticles(particle.getKey().toString(), probability);
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

    private JsonObject toOverridenEnvironnementAttribute(JsonElement value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("modifier", "override");
        obj.add("argument", value);
        return obj;
    }
}
