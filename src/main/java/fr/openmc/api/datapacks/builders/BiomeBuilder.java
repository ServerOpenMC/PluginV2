package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * Exemple simple d'un biome:
 * {
 *   "attributes": {},
 *   "carvers": [],
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
 *   "temperature": 2
 * }
 */
public final class BiomeBuilder implements ContentBuilder {
    private JsonObject attributes = new JsonObject();
    private final JsonArray carvers = new JsonArray();
    @Getter
    private final JsonObject effects = new JsonObject();
    private final JsonArray features = new JsonArray();
    private String temperatureModifier = "none";
    private Float downfall = 0.5f;
    private Float temperatures = 0.5f;
    private Boolean hasPrecipitation = true;

    public BiomeBuilder attributes(EnvironnementAttributeBuilder builder) {
        if (attributes == null) {
            this.attributes = builder.getOutputData();
        } else {
            for (var entry : builder.getOutputData().entrySet()) {
                if (!attributes.has(entry.getKey()))
                    attributes.add(entry.getKey(), entry.getValue());
            }
        }
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
        this.temperatureModifier = id;
        return this;
    }

    public BiomeBuilder downfall(Float value) {
        this.downfall = value;
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

    public BiomeBuilder waterColor(Integer color) {
        this.effects.addProperty("water_color", color);
        return this;
    }

    public BiomeBuilder grassColor(Integer color) {
        this.effects.addProperty("grass_color", color);
        return this;
    }

    public BiomeBuilder foliageColor(Integer color) {
        this.effects.addProperty("foliage_color", color);
        return this;
    }

    public BiomeBuilder dryFoliageColor(Integer color) {
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

    public BiomeBuilder temperatures(Float value) {
        this.temperatures=value;
        return this;
    }

    public BiomeBuilder hasPrecipitation(Boolean bool) {
        this.hasPrecipitation=bool;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (attributes != null) json.add("attributes", attributes);
        if (temperatureModifier != null) json.addProperty("temperature_modifier", temperatureModifier);
        json.add("carvers", carvers);
        if (downfall != null) json.addProperty("downfall", downfall);
        JsonObject effect = effects;
        if (effect.get("water_color") == null)
            effect.addProperty("water_color", "#000000");
        json.add("effects", effects);
        json.add("features", features);
        if (hasPrecipitation != null) json.addProperty("has_precipitation", hasPrecipitation);
        if (temperatures != null) json.addProperty("temperature", temperatures);

        return json;
    }
}
