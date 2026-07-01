package fr.openmc.core.registry.ambient.contents;

import fr.openmc.api.datapacks.injectors.DimensionTypesInjector;
import fr.openmc.core.registry.ambient.CustomAmbient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Particle;

public class HellAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "hell_ambient";
    }

    @Override
    public DimensionTypesInjector.DimensionTypeBuilder getDimensionTypeBuilder() {
        return new DimensionTypesInjector.DimensionTypeBuilder()
                .attributes(obj -> {
                    obj.addProperty("visual/ambient_light_color", "#A3170B");
                    obj.addProperty("visual/block_light_tint", "#F53200");
                    obj.addProperty("visual/fog_start_distance", 10);
                    obj.addProperty("visual/fog_end_distance", 96);
                    obj.addProperty("minecraft:visual/sky_light_color", "#7a7aff");
                    obj.addProperty("minecraft:visual/sky_light_factor", 0);
                    obj.addProperty("visual/fog_color","#5E1414");
                })
                .ambientParticles(Particle.CRIMSON_SPORE, 0.25f)
                .defaultClock(null)
                .ambientLight(0.1f)
                .cardinalLight("nether")
                .timelines("#minecraft:in_nether")
                .skybox(DimensionType.Skybox.NONE)
                .infiniburn("#minecraft:infiniburn_nether")
                .hasSkylight(false)
                .hasCeiling(true)
                .hasFixedTime(true);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.NETHER;
    }
}
