package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.ambient;

import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.ambient.CustomAmbientRegistry;
import fr.openmc.core.registry.ambient.builder.AmbientBuilder;
import fr.openmc.core.utils.nms.WeatherType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Particle;

public class WitchHouseAmbient extends CustomAmbient {
    @Override
    public String getId() {
        return "witch_house_ambient";
    }

    @Override
    public AmbientBuilder getAmbientBuilder() {
        return new AmbientBuilder(CustomAmbientRegistry.NAMESPACE, this.getId())
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/sky_light_color", "#FAED5C");
                            obj.addProperty("visual/ambient_light_color", "#001A19");

                            obj.addProperty("visual/sunrise_sunset_color", "#e540e58b");
                            obj.addProperty("visual/sky_color", "#0D0030");

                            obj.addProperty("visual/fog_start_distance", 0);
                            obj.addProperty("visual/fog_end_distance", 512);
                            obj.addProperty("visual/fog_color", "#3C374A");
                            obj.addProperty("visual/water_fog_color", "#3D2E42");

                            obj.addProperty("visual/cloud_height", 240);
                        })
                        .ambientParticles(Particle.WITCH, 0.02)
                        .ambientParticles(Particle.DRAGON_BREATH, 0.02)
//                        .ambientSounds(new AmbientSoundBuilder()
//                                .additions(new AmbientSoundBuilder.AdditionsBuilder()
//                                        .sound("minecraft:ambient.underwater.loop.additions.rare")
//                                        .tickChance(0.001)
//                                )
//                        )
                )
                .ambientLight(0.2f)
                .skybox(DimensionType.Skybox.OVERWORLD)
                .hasSkylight(true)
                .waterColor("#604f7d")
                .defaultClock("overworld")
                .timelines("#minecraft:in_overworld")
                .hasFixedTime(true, 200)
                .hasPrecipitation(true, WeatherType.RAIN);
    }

    @Override
    public ResourceKey<Level> getTransitionDimension() {
        return Level.END;
    }
}
