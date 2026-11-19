package fr.openmc.core.features.events.contents.christmas.contents.ambient;


import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.api.datapacks.builders.sounds.AmbientSoundBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import fr.openmc.core.utils.nms.WeatherType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Particle;

public class BlizzardAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "blizzard_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/sky_color", "#9fc4ff");

                            obj.addProperty("visual/fog_color", "#dbe9ff");
                            obj.addProperty("visual/fog_start_distance", 0);
                            obj.addProperty("visual/fog_end_distance", 67);

                            obj.addProperty("visual/cloud_height", 100);
                            obj.addProperty("visual/cloud_color", "#99dbe9ff");
                        })
                        .ambientParticles(Particle.SNOWFLAKE, 0.007)
                        .ambientSounds(new AmbientSoundBuilder()
                                .additions(new AmbientSoundBuilder.AdditionsBuilder()
                                        .sound("minecraft:item.elytra.flying")
                                        .tickChance(0.0005)
                                )
                        )
                )
                .temperatures(-0.8F)
                .waterColor("#0b2a5c")
                .skybox(DimensionType.Skybox.OVERWORLD)
                .hasSkylight(true)
                .defaultClock("overworld")
                .timelines("#minecraft:in_overworld")
                .hasFixedTime(true, 6000)
                .hasPrecipitation(true, WeatherType.RAIN);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
