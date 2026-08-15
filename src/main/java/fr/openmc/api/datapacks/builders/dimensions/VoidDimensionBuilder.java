package fr.openmc.api.datapacks.builders.dimensions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;

/**
 * Exemple simple d'une dimension vide, le but n'est pas de faire une API
 * pour build des datapacks sans toucher a un .json et ttes la structure qu'il y a derriere:
 * {
 *   "type": "minecraft:overworld",
 *   "generator": {
 *     "type": "minecraft:flat",
 *     "settings": {
 *       "biome": "draft:draft",
 *       "lakes": false,
 *       "features": false,
 *       "layers": []
 *     }
 *   }
 * }
 */
public final class VoidDimensionBuilder implements DimensionBuilder {
    private String type = "minecraft:overworld";
    private String biome = "minecraft:plains";

    public VoidDimensionBuilder type(DimensionTypesInjector injector) {
        this.type = injector.getKey();
        return this;
    }

    public VoidDimensionBuilder biome(BiomesInjector injector) {
        this.biome = injector.getKey();
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (type != null) json.addProperty("type", type);
        if (biome != null) {
            JsonObject generator = new JsonObject();
            generator.addProperty("type", "minecraft:flat");

            JsonObject settings = new JsonObject();
            settings.addProperty("biome", biome);
            settings.addProperty("lakes", false);
            settings.addProperty("features", false);
            settings.add("layers", new JsonArray());

            generator.add("settings", settings);
            json.add("generator", generator);
        }

        return json;
    }
}