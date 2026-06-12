package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class GoldenAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "golden_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
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
                        .particleDustColorTransition(16776172, 16766720, 2, 0.01)
                )
                .hasSkylight(true)
                .timelines(new TimelineBuilder()
                        .clock("minecraft:overworld")
                        .periodTicks(24000))
                .biomes(new BiomeBuilder());
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
