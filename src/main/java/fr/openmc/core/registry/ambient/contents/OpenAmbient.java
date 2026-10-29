package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import fr.openmc.core.utils.nms.WeatherType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Particle;

public class OpenAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "open_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/block_light_tint", "#FCD4FC");
                            obj.addProperty("visual/ambient_light_color", "#190721");
                            obj.addProperty("visual/sky_light_color", "#FA8DFC");

                            obj.addProperty("visual/sky_color", "#8AC1FF");
                            obj.addProperty("visual/sunrise_sunset_color", "#4cf6c3fa");

                            obj.addProperty("visual/fog_color", "#D8C5DB");
                            obj.addProperty("visual/fog_start_distance", 70);
                            obj.addProperty("visual/fog_end_distance", 80);

                            obj.addProperty("visual/cloud_height", 230);
                            obj.addProperty("visual/cloud_color", "#ccfffcff");
                        })
                        .ambientParticles(Particle.FIREFLY, 0.008)
                        .particleDustColorTransition(16777215, 15822847, 2, 0.007)
                )
                .skybox(DimensionType.Skybox.OVERWORLD)
                .hasSkylight(true)
                .defaultClock("overworld")
                .timelines("#minecraft:in_overworld")
                .hasPrecipitation(false, WeatherType.NONE);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}