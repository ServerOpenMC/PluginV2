package fr.openmc.core.features.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DreamDimensionManager {

    public static final String DIMENSION_NAME = "world_omc_dream_dream";
    public static World DIMENSION_WORLD;

    private static File seedFile;
    private static FileConfiguration seedConfig;
    private static boolean seedChanged = false;

    public static void init() {
        seedFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "seed.yml");
        loadSeed();
        DIMENSION_WORLD = Bukkit.getWorld(DIMENSION_NAME);
    }

    public static void save() {
        OMCLogger.info("[DreamDimensionManager] Saving seed: {}", DIMENSION_WORLD.getSeed());
        saveSeed(DIMENSION_WORLD.getSeed());
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
        if (DIMENSION_WORLD == null) return;

        long current = DIMENSION_WORLD.getSeed();

        if (saved == -1) {
            saveSeed(current);
            seedChanged = false;
            return;
        }

        seedChanged = saved != current;

        if (seedChanged) {
            saveSeed(current);
        }
    }

    public static boolean hasSeedChanged() {
        return seedChanged;
    }
}
