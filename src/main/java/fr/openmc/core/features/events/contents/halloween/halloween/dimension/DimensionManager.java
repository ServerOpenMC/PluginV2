package fr.openmc.core.features.events.contents.halloween.halloween.dimension;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.dream.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;

import java.io.File;

public class DimensionManager {

    public static final String DIMENSION_NAME = "world_omc_halloween_witch_house";
    public static World HALLOWEEN_WORLD;

    public static void init() {
//        seedFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "seed.yml");
//        loadSeed();
        HALLOWEEN_WORLD = Bukkit.getWorld(DIMENSION_NAME);

        setupDimension();
    }

    //TODO setup la dimension :
    // - monde vide + ajout de la structure de Mcross
    // - ambiant custom

    private static void setupDimension() {
        if (!HALLOWEEN_WORLD.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        // ** SPAWNING RULES **
        HALLOWEEN_WORLD.setSpawnLimit(SpawnCategory.MONSTER, 10);
        HALLOWEEN_WORLD.setSpawnLimit(SpawnCategory.AMBIENT, 10);
        HALLOWEEN_WORLD.setSpawnLimit(SpawnCategory.ANIMAL, 6);

        HALLOWEEN_WORLD.setTicksPerSpawns(SpawnCategory.MONSTER, 30);
        HALLOWEEN_WORLD.setTicksPerSpawns(SpawnCategory.AMBIENT, 15);
        HALLOWEEN_WORLD.setTicksPerSpawns(SpawnCategory.ANIMAL, 30);

        // ** SET GAMERULE FOR THE WORLD **
        HALLOWEEN_WORLD.setGameRule(GameRules.ADVANCE_TIME, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.ADVANCE_WEATHER, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.RAIDS, true);
        HALLOWEEN_WORLD.setGameRule(GameRules.SPAWN_PATROLS, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.LOCATOR_BAR, false);
        HALLOWEEN_WORLD.setGameRule(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false);

        // ** SET WORLD BORDER AND TIME **
        HALLOWEEN_WORLD.getWorldBorder().setSize(10000);
        HALLOWEEN_WORLD.setTime(18000);

        OMCLogger.infoFormatted("Dimension des rêves setup (gamerules, worldborder, time)");
    }

}
