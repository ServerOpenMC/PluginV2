package fr.openmc.core.features.singularity.contents.worldtemplates;

import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;
import fr.openmc.core.registry.worldtemplates.WorldTemplate;
import fr.openmc.core.registry.worldtemplates.interfaces.HasGamerules;
import fr.openmc.core.registry.worldtemplates.interfaces.HasWorldBorder;
import org.bukkit.GameRule;
import org.bukkit.GameRules;

import java.util.HashMap;
import java.util.Map;

public class SingularityWorldTemplate extends WorldTemplate
        implements HasWorldBorder, HasGamerules {
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
        return new DimensionTypeBuilder();
    }

    @Override
    public BiomeBuilder biome() {
        return new BiomeBuilder();
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
}
