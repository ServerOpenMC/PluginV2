package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DarkAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "dark_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/ambient_light_color", "#DD37E6");
                            obj.addProperty("visual/sky_color", "#DD37E6");
                            obj.addProperty("visual/sky_light_color", "#3B205E");
                            obj.addProperty("visual/fog_start_distance", 40);
                            obj.addProperty("visual/fog_end_distance", 70);
                            obj.addProperty("visual/sunrise_sunset_color", "#FFBB00FA");
                        })
                )
                .defaultClock(null)
                .timelines((String) null)
                .skybox(DimensionType.Skybox.END)
                .hasSkylight(true);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
