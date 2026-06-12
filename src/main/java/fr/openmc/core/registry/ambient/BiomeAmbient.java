package fr.openmc.core.registry.ambient;

import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.injectors.BiomesInjector;
import fr.openmc.core.utils.MathUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;

public interface BiomeAmbient {
    BiomeBuilder getBiomeBuilder();

    default BiomesInjector toBiomeInjector(Identifier ambientId) {
        return new BiomesInjector(ambientId.getNamespace()).add(ambientId.getPath(), getBiomeBuilder());
    }

    /**
     * Genere la clé de la variente du biome
     * @param initialBiomeKey clé du biome initial a changer
     * @param ambient l'ambience qui génère la variante
     * @return identifiant de la variante du biome (namespace:initialBiomePath_ambientId)
     */
    default Identifier toBiomeVariantKey(Identifier initialBiomeKey, CustomAmbient ambient) {
        return Identifier.parse(CustomAmbientRegistry.NAMESPACE + ":" + initialBiomeKey.getPath() + "_" + ambient.getId());
    }

    /**
     * Genere une variante d'un biome en fonction d'une ambience (namespace:id)
     * Retourne un injecteur de biome qui prendra une variante de celui ci
     * (couleur de l'herbe initial si pas override par l'ambience, idem pour les autres)
     * @param initialBiome le biome initial à cloner
     * @param ambientId l'id de l'ambience
     * @return l'injecteur du fichier json
     */
    default BiomesInjector toBiomeVariant(Biome initialBiome, Identifier ambientId) {
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

        return new BiomesInjector(ambientId.getNamespace()).add(ambientId.getPath(), builder);
    }

    private boolean hasEffects(JsonObject effects, String envKey) {
        return effects.get(envKey) != null;
    }
}
