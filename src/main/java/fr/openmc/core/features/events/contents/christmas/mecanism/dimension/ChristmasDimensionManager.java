package fr.openmc.core.features.events.contents.christmas.mecanism.dimension;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.task.ChristmasMusicTask;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.SpawnCategory;

import java.io.File;
import java.io.IOException;

public class ChristmasDimensionManager {

    public static final String DIMENSION_NAME = "world_omc_christmas_christmas";
    public static World CHRISTMAS_WORLD;

    private static File seedFile;
    private static FileConfiguration seedConfig;
    private static boolean seedChanged = false;

    private static final ChristmasMusicTask CHRISTMAS_MUSIC_TASK = new ChristmasMusicTask();

    public static void init() {
        seedFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/christmas", "seed.yml");
        loadSeed();
        CHRISTMAS_WORLD = Bukkit.getWorld(DIMENSION_NAME);

        setupDimension();
        CHRISTMAS_MUSIC_TASK.start();
    }

    public static void save() {
        OMCLogger.info("[ChristmasDimensionManager] Saving seed: {}", CHRISTMAS_WORLD.getSeed());
        saveSeed(CHRISTMAS_WORLD.getSeed());
        CHRISTMAS_MUSIC_TASK.stop();
    }

    private static void setupDimension() {
        if (!CHRISTMAS_WORLD.getName().equals(DIMENSION_NAME)) return;

        ChristmasDimensionManager.checkSeed();

        if (ChristmasDimensionManager.hasSeedChanged()) {
            // ** SPAWNING RULES **
            CHRISTMAS_WORLD.setSpawnLimit(SpawnCategory.MONSTER, 10);
            CHRISTMAS_WORLD.setSpawnLimit(SpawnCategory.AMBIENT, 10);
            CHRISTMAS_WORLD.setSpawnLimit(SpawnCategory.ANIMAL, 6);

            CHRISTMAS_WORLD.setTicksPerSpawns(SpawnCategory.MONSTER, 30);
            CHRISTMAS_WORLD.setTicksPerSpawns(SpawnCategory.AMBIENT, 15);
            CHRISTMAS_WORLD.setTicksPerSpawns(SpawnCategory.ANIMAL, 30);

            // ** SET GAMERULE FOR THE WORLD **
            CHRISTMAS_WORLD.setGameRule(GameRules.RAIDS, true);
            CHRISTMAS_WORLD.setGameRule(GameRules.SPAWN_PATROLS, false);
            CHRISTMAS_WORLD.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
            CHRISTMAS_WORLD.setGameRule(GameRules.LOCATOR_BAR, false);
            CHRISTMAS_WORLD.setGameRule(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false);

            // ** SET WORLD BORDER AND TIME **
            CHRISTMAS_WORLD.getWorldBorder().setSize(20000);

            OMCLogger.infoFormatted("Dimension de noel setup (gamerules, worldborder, time)");
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
            OMCLogger.error("Cannot save seed christmas_world", e);
        }
    }

    public static void checkSeed() {
        long saved = seedConfig.getLong("world_seed", -1);
        if (CHRISTMAS_WORLD == null) return;

        long current = CHRISTMAS_WORLD.getSeed();

        if (saved == -1) {
            saveSeed(current);
            seedChanged = true;
            return;
        }

        seedChanged = saved != current;

        if (seedChanged) {
            OMCLogger.info("La seed de la dimension de noel a changé");
            saveSeed(current);
        }
    }

    public static boolean hasSeedChanged() {
        return seedChanged;
    }
}