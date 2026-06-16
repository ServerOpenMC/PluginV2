package fr.openmc.core.registry.ambient.builder;

import com.google.gson.JsonObject;
import fr.openmc.api.datapacks.OMCDatapack;
import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.api.datapacks.injectors.TimelinesInjector;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.utils.nms.WeatherType;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.function.Consumer;

public class AmbientBuilder {
    private final String namespace;
    private final String id;

    @Getter
    private final DimensionTypeBuilder dimTypeBuilder = new DimensionTypeBuilder();
    @Getter
    private TimelineBuilder timelineBuilder = null;
    @Getter
    private BiomeBuilder biomeBuilder = null;
    @Getter
    private Integer timeFixed = null;

    @Getter
    private WeatherType weatherFixed = null;

    public AmbientBuilder(String namepace, String id) {
        this.namespace = namepace;
        this.id = id;
    }

    public boolean utilizeBiome() {
        return biomeBuilder != null;
    }


    public AmbientBuilder ambientLight(double ambientLight) {
        this.dimTypeBuilder.ambientLight(ambientLight);
        return this;
    }

    public AmbientBuilder attributesBuilder(EnvironnementAttributeBuilder builder) {
        this.dimTypeBuilder.attributesBuilder(builder);
        return this;
    }

    public AmbientBuilder defaultClock(String keyOfClock) {
        this.dimTypeBuilder.defaultClock(keyOfClock);
        return this;
    }

    public AmbientBuilder hasCeiling(boolean hasCeiling) {
        this.dimTypeBuilder.hasCeiling(hasCeiling);
        return this;
    }

    public AmbientBuilder hasSkylight(boolean hasSkylight) {
        this.dimTypeBuilder.hasSkylight(hasSkylight);
        return this;
    }

    public AmbientBuilder hasFixedTime(boolean hasFixedTime, int timeSet) {
        this.dimTypeBuilder.hasFixedTime(hasFixedTime);
        this.timeFixed = timeSet;
        return this;
    }

    public AmbientBuilder hasFixedTime(boolean hasFixedTime) {
        this.dimTypeBuilder.hasFixedTime(hasFixedTime);
        return this;
    }
    public AmbientBuilder skybox(String skybox) {
        this.dimTypeBuilder.skybox(skybox);
        return this;
    }

    public AmbientBuilder skybox(DimensionType.Skybox skybox) {
        return skybox(skybox.getSerializedName());
    }

    public AmbientBuilder cardinalLight(String cardinalLight) {
        this.dimTypeBuilder.cardinalLight(cardinalLight);
        return this;
    }

    public AmbientBuilder timelines(String timelines) {
        this.dimTypeBuilder.timelines(timelines);
        return this;
    }

    public AmbientBuilder timelines(TimelineBuilder builder) {
        this.dimTypeBuilder.timelines(new TimelinesInjector(namespace).add(id, builder));
        this.timelineBuilder = builder;
        return this;
    }

    public AmbientBuilder biomes(BiomeBuilder builder) {
        this.biomeBuilder = builder;
        return this;
    }

    public AmbientBuilder downfall(Float value) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.downfall(value);
        return this;
    }

    public AmbientBuilder temperatures(Float value) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.temperatures(value);
        return this;
    }

    public AmbientBuilder temperatureModifier(String id) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.temperatureModifier(id);
        return this;
    }

    public AmbientBuilder hasPrecipitation(Boolean bool) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.hasPrecipitation(bool);
        return this;
    }

    public AmbientBuilder hasPrecipitation(Boolean bool, WeatherType type) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.hasPrecipitation(bool);
        this.weatherFixed = type;
        return this;
    }

    public AmbientBuilder effects(Consumer<JsonObject> builder) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.effects(builder);
        return this;
    }

    public AmbientBuilder waterColor(String color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.waterColor(color);
        return this;
    }

    public AmbientBuilder grassColor(String color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.grassColor(color);
        return this;
    }

    public AmbientBuilder foliageColor(String color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.foliageColor(color);
        return this;
    }

    public AmbientBuilder dryFoliageColor(String color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.dryFoliageColor(color);
        return this;
    }

    public AmbientBuilder waterColor(Integer color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.waterColor(color);
        return this;
    }

    public AmbientBuilder grassColor(Integer color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.grassColor(color);
        return this;
    }

    public AmbientBuilder foliageColor(Integer color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.foliageColor(color);
        return this;
    }

    public AmbientBuilder dryFoliageColor(Integer color) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.dryFoliageColor(color);
        return this;
    }

    /**
     * Set la grass color modifier
     * @param id none, dark_forest, swamp
     * @return le builder
     */
    public AmbientBuilder grassColorModifier(String id) {
        if (biomeBuilder == null) biomeBuilder = new BiomeBuilder();
        this.biomeBuilder.grassColorModifier(id);
        return this;
    }

    public void runInjectors(CustomAmbient ambient, OMCDatapack datapack) {
        // ** DimensionType Injector
        DimensionTypesInjector dimensionTypesInjector = new DimensionTypesInjector(namespace).add(id, dimTypeBuilder);
        datapack.addInjector(dimensionTypesInjector);

        // ** Timeline Injector
        if (timelineBuilder != null) {
            TimelinesInjector timelinesInjector = new TimelinesInjector(namespace).add(id, timelineBuilder);
            datapack.addInjector(timelinesInjector);
        }

        // ** Biome Injector
        if (this.utilizeBiome()) {
            net.minecraft.core.Registry<Biome> biomeRegistry =
                    MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.BIOME);

            // ** Création de chaque variante des biomes existants.
            // * Renvoie simplement un biome ayant les effects (grassColor, waterColor, ...) lié au biome original
            // * si ça pas été override par le biome mis par le CustomAmbient
            for (var biomeEntry : biomeRegistry.entrySet()) {
                ResourceKey<Biome> key = biomeEntry.getKey();
                if (key.identifier().getNamespace().equals(CustomAmbientRegistry.NAMESPACE)) continue; // * On skip les biomes de notre datapack

                Biome biome = biomeEntry.getValue();

                datapack.addInjector(ambient.toBiomeVariant(
                        biome, ambient.toBiomeVariantKey(key.identifier())));
            }
        }
    }

}
