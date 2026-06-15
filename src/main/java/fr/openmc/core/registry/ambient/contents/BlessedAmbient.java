package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Particle;

public class BlessedAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "blessed_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/sky_light_color", "#FAED5C");
                            obj.addProperty("visual/ambient_light_color", "#001A19");

                            obj.addProperty("visual/sunrise_sunset_color", "#e540e58b");
                            obj.addProperty("visual/sky_color", "#92E6FC");

                            obj.addProperty("visual/fog_start_distance", 0);
                            obj.addProperty("visual/fog_end_distance", 512);
                            obj.addProperty("visual/fog_color", "#5CFFD3");
                            obj.addProperty("visual/water_fog_color", "#75FFD1");

                            obj.addProperty("visual/cloud_height", 240);
                        })
                        .ambientParticles(Particle.OMINOUS_SPAWNING, 0.01)
                        .ambientParticles(Particle.FISHING, 0.004)
                )
                .ambientLight(0.2f)
                .skybox(DimensionType.Skybox.OVERWORLD)
                .hasSkylight(true)
                .biomes(new BiomeBuilder()
                        .waterColor("#43d5ee"))
                .defaultClock("overworld")
                .timelines("#minecraft:in_overworld")
                .hasFixedTime(true, 200);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
