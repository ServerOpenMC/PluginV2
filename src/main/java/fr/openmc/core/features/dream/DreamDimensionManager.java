package fr.openmc.core.features.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.SpawnCategory;

import java.io.File;
import java.io.IOException;

public class DreamDimensionManager {

    public static final String DIMENSION_NAME = "world_omc_dream_dream";
    public static World DREAM_WORLD;

    private static File seedFile;
    private static FileConfiguration seedConfig;
    private static boolean seedChanged = false;

    public static void init() {
        seedFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "seed.yml");
        loadSeed();
        DREAM_WORLD = Bukkit.getWorld(DIMENSION_NAME);

        setupDimension();
    }

    public static void save() {
        OMCLogger.info("[DreamDimensionManager] Saving seed: {}", DREAM_WORLD.getSeed());
        saveSeed(DREAM_WORLD.getSeed());
    }

    private static void setupDimension() {
        if (!DREAM_WORLD.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        DreamDimensionManager.checkSeed();

        if (DreamDimensionManager.hasSeedChanged()) {
            // ** SPAWNING RULES **
            DREAM_WORLD.setSpawnLimit(SpawnCategory.MONSTER, 10);
            DREAM_WORLD.setSpawnLimit(SpawnCategory.AMBIENT, 10);
            DREAM_WORLD.setSpawnLimit(SpawnCategory.ANIMAL, 6);

            DREAM_WORLD.setTicksPerSpawns(SpawnCategory.MONSTER, 30);
            DREAM_WORLD.setTicksPerSpawns(SpawnCategory.AMBIENT, 15);
            DREAM_WORLD.setTicksPerSpawns(SpawnCategory.ANIMAL, 30);

            // ** SET GAMERULE FOR THE WORLD **
            DREAM_WORLD.setGameRule(GameRules.ADVANCE_TIME, false);
            DREAM_WORLD.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            DREAM_WORLD.setGameRule(GameRules.ADVANCE_WEATHER, false);
            DREAM_WORLD.setGameRule(GameRules.RAIDS, true);
            DREAM_WORLD.setGameRule(GameRules.SPAWN_PATROLS, false);
            DREAM_WORLD.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
            DREAM_WORLD.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
            DREAM_WORLD.setGameRule(GameRules.LOCATOR_BAR, false);
            DREAM_WORLD.setGameRule(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false);

            // ** SET WORLD BORDER AND TIME **
            DREAM_WORLD.getWorldBorder().setSize(10000);
            DREAM_WORLD.setTime(18000);

            OMCLogger.infoFormatted("Dimension des rêves setup (gamerules, worldborder, time)");
        }
    }

    private static void loadSeed() {
        if (!seedFile.exists()) {
            OMCLogger.info("Fichier seed.yml manquant, il sera créé au saveSeed().");
        }
        seedConfig = YamlConfiguration.loadConfiguration(seedFile);
    }

    private static void saveSeed(long seed) {
        seedConfig.set("world_seed", seed);
        try {
            seedConfig.save(seedFile);
        } catch (IOException e) {
            OMCLogger.error("Cannot save seed dream_world", e);
        }
    }

    public static void checkSeed() {
        long saved = seedConfig.getLong("world_seed", -1);
        if (DREAM_WORLD == null) return;

        long current = DREAM_WORLD.getSeed();

        if (saved == -1) {
            saveSeed(current);
            seedChanged = true;
            return;
        }

        seedChanged = saved != current;

        if (seedChanged) {
            OMCLogger.info("La seed de la dimension des rêves a changé");
            saveSeed(current);
        }
    }

    public static boolean hasSeedChanged() {
        return seedChanged;
    }
}
