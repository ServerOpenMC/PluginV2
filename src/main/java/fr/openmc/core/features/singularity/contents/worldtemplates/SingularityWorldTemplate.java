package fr.openmc.core.features.singularity.contents.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.api.datapacks.builders.EnvironnementAttributeBuilder;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasFeature;
import fr.openmc.core.features.singularity.sub.worldsfx.SingularityWorldManager;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;
import fr.openmc.core.registry.worldtemplates.interfaces.HasGamerules;
import fr.openmc.core.registry.worldtemplates.interfaces.HasWorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.Map;

public class SingularityWorldTemplate extends WorldTemplate
        implements HasWorldBorder, HasGamerules, HasFeature {
    @Override
    public String getNamespace() {
        return "omc_singularity";
    }

    @Override
    public String getId() {
        return "singularity_world";
    }

    @Override
    public DimensionTypeBuilder dimensionType() {
        return new DimensionTypeBuilder()
                .attributesBuilder(new EnvironnementAttributeBuilder()
                        .attributes(obj -> {
                            obj.addProperty("visual/ambient_light_color", "#C4C4C4");
                            obj.addProperty("visual/block_light_tint", "#1AFFE4");
                            obj.addProperty("visual/night_vision_color", "#61F5FF");

                            obj.addProperty("visual/fog_start_distance", 64);
                            obj.addProperty("visual/fog_end_distance", 174);

                            obj.addProperty("minecraft:visual/sky_light_color", "#f7f7f7");
                            obj.addProperty("visual/fog_color","#E8E8E8");
                        })
                        .ambientParticles(Particle.ENCHANT, 0.02f)
                        .ambientParticles(Particle.FLASH, 0.0007f, Map.of("color", 14342874)))
                .defaultClock(null)
                .ambientLight(0f)
                .cardinalLight("nether")
                .timelines("#minecraft:in_nether")
                .skybox(DimensionType.Skybox.NONE)
                .hasSkylight(true)
                .hasCeiling(true)
                .hasFixedTime(true);
    }

    @Override
    public BiomeBuilder biome() {
        return new BiomeBuilder()
                .grassColor("#ADADAD");
    }

    @Override
    public Map<GameRule<?>, Object> getGamerules() {
        Map<GameRule<?>, Object> gamerules = new HashMap<>();

        gamerules.put(GameRules.ADVANCE_TIME, Boolean.FALSE);
        gamerules.put(GameRules.ADVANCE_WEATHER, Boolean.FALSE);
        gamerules.put(GameRules.LOCATOR_BAR, Boolean.FALSE);
        gamerules.put(GameRules.PVP, Boolean.FALSE);
        gamerules.put(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, Boolean.FALSE);

        gamerules.put(GameRules.TNT_EXPLODES, Boolean.FALSE);

        gamerules.put(GameRules.SPAWN_MOBS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWN_MONSTERS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWN_PATROLS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWN_PHANTOMS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWN_WARDENS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWN_WANDERING_TRADERS, Boolean.FALSE);
        gamerules.put(GameRules.SPAWNER_BLOCKS_WORK, Boolean.FALSE);

        return gamerules;
    }

    @Override
    public double[] getCenter() {
        double[] center = new double[2];
        center[0] = 0;
        center[1] = 0;
        return center;
    }

    @Override
    public double getSize() {
        return 5000;
    }

    @Override
    public Feature getFeature() {
        return new SingularityWorldManager(this);
    }
}
