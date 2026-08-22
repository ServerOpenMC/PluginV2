package fr.openmc.api.datapacks.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.builders.sounds.AmbientSoundBuilder;
import org.bukkit.Particle;

import java.util.Map;
import java.util.function.Consumer;

public class EnvironnementAttributeBuilder {
    private final JsonObject attributes = new JsonObject();

    public JsonObject getOutputData() {
        return attributes;
    }

    public EnvironnementAttributeBuilder attributes(Consumer<JsonObject> builder) {
        JsonObject obj = new JsonObject();
        builder.accept(obj);
        for (var entry : obj.entrySet()) {
            this.attributes.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public EnvironnementAttributeBuilder attributes(JsonObject attributes) {
        for (var entry : attributes.entrySet()) {
            this.attributes.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Ajoute un attribut "minecraft:visual/ambient_particles" simple.
     * Exemple :
     * "minecraft:visual/ambient_particles": [ { "particle": { "type": "minecraft:crimson_spore" }, "probability": 0.025 } ]
     */
    public EnvironnementAttributeBuilder ambientParticles(String particleType, double probability) {
        return ambientParticles(particleType, probability, null);
    }

    /**
     * Ajoute un attribut "minecraft:visual/ambient_particles" simple.
     * Exemple :
     * "minecraft:visual/ambient_particles": [ { "particle": { "type": "minecraft:crimson_spore" }, "probability": 0.025 } ]
     */
    public EnvironnementAttributeBuilder ambientParticles(String particleType, double probability, Map<String, Integer> options) {
        JsonObject entry = new JsonObject();
        JsonObject particle = new JsonObject();
        particle.addProperty("type", particleType);

        if (options != null) {
            for (var entry1 : options.entrySet()) {
                particle.addProperty(entry1.getKey(), entry1.getValue());
            }
        }

        entry.add("particle", particle);
        entry.addProperty("probability", probability);

        String key = "minecraft:visual/ambient_particles";
        if (this.attributes.has(key)) {
            this.attributes.getAsJsonArray(key).add(entry);
        } else {
            JsonArray arr = new JsonArray();
            arr.add(entry);
            this.attributes.add(key, arr);
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
    public EnvironnementAttributeBuilder particleDustColorTransition(int fromColor, int toColor, float scale, double probability) {
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
            this.attributes.getAsJsonArray(key).add(entry);
        } else {
            JsonArray arr = new JsonArray();
            arr.add(entry);
            this.attributes.add(key, arr);
        }
        return this;
    }

    /**
     * Ajoute un attribut "minecraft:visual/ambient_particles" simple.
     * Exemple :
     * "minecraft:visual/ambient_particles": [ { "particle": { "type": "minecraft:crimson_spore" }, "probability": 0.025 } ]
     */
    public EnvironnementAttributeBuilder ambientParticles(Particle particle, double probability) {
        return ambientParticles(particle.getKey().toString(), probability);
    }

    public EnvironnementAttributeBuilder ambientParticles(Particle particle, double probability, Map<String, Integer> options) {
        return ambientParticles(particle.getKey().toString(), probability, options);
    }

    public EnvironnementAttributeBuilder ambientSounds(AmbientSoundBuilder ambientBuilder) {
        this.attributes.add("audio/ambient_sounds", ambientBuilder.toJson());
        return this;
    }
}
