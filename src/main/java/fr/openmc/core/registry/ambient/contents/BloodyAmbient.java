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

public class BloodyAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "bloody_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/block_light_tint", "#F53200");
                            obj.addProperty("visual/ambient_light_color", "#FF5C57");
                            obj.addProperty("minecraft:visual/sky_light_factor", 0.7);

                            obj.addProperty("visual/fog_start_distance", 40);
                            obj.addProperty("visual/fog_end_distance", 70);
                            obj.addProperty("visual/fog_color","#800000");

                            obj.addProperty("visual/moon_angle", 67);
                            obj.addProperty("visual/star_brightness", 0.7);

                            obj.addProperty("visual/cloud_height", 70);
                            obj.addProperty("visual/cloud_color", "#e58c2b2b");

                            obj.addProperty("visual/water_fog_color", "#330505");
                        })
                        .ambientParticles(Particle.CRIMSON_SPORE, 0.01f)
                        .ambientParticles(Particle.RAID_OMEN, 0.002f))
                .ambientLight(0f)
                .cardinalLight("nether")
                .skybox(DimensionType.Skybox.OVERWORLD)
                .hasSkylight(false)
                .hasCeiling(true)
                .biomes(new BiomeBuilder()
                        .hasPrecipitation(false)
                        .waterColor("#F24545"))

                .defaultClock("overworld")
                .timelines("#minecraft:in_overworld")
                .hasFixedTime(true, 21000);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.NETHER;
    }
}
