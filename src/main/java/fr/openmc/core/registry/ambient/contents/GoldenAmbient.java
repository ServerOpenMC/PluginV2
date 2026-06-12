package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import fr.openmc.core.registry.ambient.BiomeAmbient;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.TimelineAmbient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class GoldenAmbient extends CustomAmbient implements TimelineAmbient, BiomeAmbient {
    @Override
    public String getId() {
        return "golden_ambient";
    }

    @Override
    public DimensionTypeBuilder getDimensionTypeBuilder() {
        return new DimensionTypeBuilder()
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/ambient_light_color", "#FFE75C");
                            obj.addProperty("visual/sky_color", "#FFD700");
                            obj.addProperty("visual/sky_light_color", "#FFE02E");
                            obj.addProperty("visual/fog_color", "#6D6319");
                            obj.addProperty("visual/fog_start_distance", 55);
                            obj.addProperty("visual/fog_end_distance", 65);
                            obj.addProperty("visual/sunrise_sunset_color", "#ccff5900");
                            obj.addProperty("visual/cloud_height", 100);
                            obj.addProperty("visual/cloud_color", "#4cffde50");
                        })
                        .particleDustColorTransition(16776172, 16766720, 2, 0.01))
                .hasSkylight(true)
                .timelines(toTimelineInjector(CustomAmbientRegistry.NAMESPACE, getId()));
    }

    @Override
    public BiomeBuilder getBiomeBuilder() {
        return new BiomeBuilder()
                .waterColor("#3f76e4");
    }

    @Override
    public TimelineBuilder getTimelineBuilder() {
        return new TimelineBuilder()
                .clock("minecraft:overworld")
                .periodTicks(24000);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
