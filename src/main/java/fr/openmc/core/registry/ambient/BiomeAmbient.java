package fr.openmc.core.registry.ambient;

import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.core.utils.MathUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;

public interface BiomeAmbient {
    BiomeBuilder getBiomeBuilder();

    default BiomesInjector toBiomeInjector(String namespace, String id) {
        return new BiomesInjector(namespace).add(id, getBiomeBuilder());
    }

    /**
     * Genere une variante d'un biome en fonction d'une ambience (namespace:id)
     * Retourne un injecteur de biome qui prendra une variante de celui ci
     * (couleur de l'herbe initial si pas override par l'ambience, idem pour les autres)
     * @param initialBiome le biome initial à cloner
     * @param namespace le namespace de l'ambience (généralement "omc_ambient")
     * @param id l'id de l'ambience
     * @return l'injecteur du fichier json
     */
    default BiomesInjector toBiomeVariant(Biome initialBiome, String namespace, String id) {
        BiomeSpecialEffects initialEffects = initialBiome.getSpecialEffects();
        Biome.ClimateSettings climate = initialBiome.climateSettings;

        JsonObject effects = getBiomeBuilder().getEffects();
        Optional<Integer> grassColor = hasEffects(effects, "grass_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("grass_color").getAsString())) :
                initialEffects.grassColorOverride();
        Optional<Integer> foliageColor = hasEffects(effects, "foliage_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("foliage_color").getAsString())) :
                initialEffects.foliageColorOverride();
        Integer waterColor = hasEffects(effects, "water_color") ?
                MathUtils.hexToInt(effects.get("water_color").getAsString()) :
                initialEffects.grassColorOverride().orElse(4159204);
        Optional<Integer> dryFoliageColor = hasEffects(effects, "dry_foliage_color") ?
                Optional.of(MathUtils.hexToInt(effects.get("dry_foliage_color").getAsString())) :
                initialEffects.foliageColorOverride();
        String grassColorModifier = hasEffects(effects, "grass_color_modifier") ?
                effects.get("grass_color_modifier").getAsString() :
                initialEffects.grassColorModifier().getName();

        BiomeBuilder builder = new BiomeBuilder()
                .waterColor(waterColor)
                .grassColorModifier(grassColorModifier)
                .hasPrecipitation(climate.hasPrecipitation())
                .downfall(climate.downfall())
                .temperatures(climate.temperature())
                .temperatureModifier(climate.temperatureModifier().getName());

        grassColor.ifPresent(builder::grassColor);
        foliageColor.ifPresent(builder::foliageColor);
        dryFoliageColor.ifPresent(builder::dryFoliageColor);

        return new BiomesInjector(namespace).add(id, builder);
    }

    private boolean hasEffects(JsonObject effects, String envKey) {
        return effects.get(envKey) != null;
    }
}
