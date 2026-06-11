package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.api.datapacks.injectors.TimelineInjector;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.TimelineAmbient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class GoldenAmbient extends CustomAmbient implements TimelineAmbient {
    @Override
    public String getId() {
        return "golden_ambient";
    }

    @Override
    public DimensionTypesInjector.DimensionTypeBuilder getDimensionTypeBuilder() {
        return new DimensionTypesInjector.DimensionTypeBuilder()
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
                .hasSkylight(true)
                .timelines(toTimelineInjector("omc_ambient", getId()))
                .particleDustColorTransition(16776172, 16766720, 2, 0.01);
    }

    @Override
    public TimelineInjector.TimelineBuilder getTimelineBuilder() {
        return new TimelineInjector.TimelineBuilder()
                .clock("minecraft:overworld")
                .periodTicks(24000);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
