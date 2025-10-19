package fr.openmc.core.features.dream.generation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.*;
import fr.openmc.core.features.dream.generation.populators.glacite.GlaciteGeodePopulator;
import fr.openmc.core.features.dream.generation.populators.glacite.GroundSpikePopulator;
import fr.openmc.core.features.dream.generation.populators.glacite.VerticalSpikePopulator;
import fr.openmc.core.features.dream.generation.populators.mud.RockPopulator;
import fr.openmc.core.features.dream.generation.populators.plains.PlainsTreePopulator;
import fr.openmc.core.features.dream.generation.populators.soulforest.SoulTreePopulator;
import fr.openmc.core.features.dream.generation.structures.cloud.CloudCastleStructure;
import fr.openmc.core.features.dream.generation.structures.glacite.BaseCampStructure;
import fr.openmc.core.features.dream.generation.structures.soulforest.SoulAltarStructure;
import fr.openmc.core.utils.structure.FeaturesPopulator;
import fr.openmc.core.utils.structure.SchematicsUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DreamDimensionManager {

    public static final String DIMENSION_NAME = "world_dream";
    private static OMCPlugin plugin;

    private static File seedFile;
    private static FileConfiguration seedConfig;

    private static final Set<FeaturesPopulator> registeredFeatures = new HashSet<>();

    public static void init() {
        plugin = OMCPlugin.getInstance();

        // ** STRUCTURES SCHEMATICS REGISTER **
        SchematicsUtils.extractSchematic(CloudCastleStructure.STRUCTURE_NAME);
        SchematicsUtils.extractSchematic(BaseCampStructure.STRUCTURE_NAME);
        SchematicsUtils.extractSchematic(SoulAltarStructure.STRUCTURE_NAME);

        // ** REGISTER STRUCTURES NBT **
        registrerFeatures(new RockPopulator());
        registrerFeatures(new PlainsTreePopulator());
        registrerFeatures(new SoulTreePopulator());
        registrerFeatures(new VerticalSpikePopulator());
        registrerFeatures(new GroundSpikePopulator());
        registrerFeatures(new GlaciteGeodePopulator());

        createDimension();

        seedFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "seed.yml");
        loadSeed();
    }

    public static void postInit() {
        World dream = Bukkit.getWorld(DIMENSION_NAME);
        if (dream == null) return;

        OMCPlugin.getInstance().getSLF4JLogger().info("Saving seed: {}", dream.getSeed());
        saveSeed(dream.getSeed());
    }

    // ** DIMENSION MANAGING **

    public static void createDimension() {
        WorldCreator creator = new WorldCreator(DIMENSION_NAME);

        File worldFolder = new File(Bukkit.getWorldContainer(), DIMENSION_NAME);
        long seed;

        if (!worldFolder.exists()) {
            seed = createSeed();
            creator.seed(seed);
            plugin.getSLF4JLogger().info("New Dream world created with seed: {}", seed);
        } else {
            World existing = Bukkit.getWorld(DIMENSION_NAME);
            seed = (existing != null) ? existing.getSeed() : creator.seed();
            plugin.getSLF4JLogger().info("Loading existing Dream world with seed: {}", seed);
        }

        creator.generator(new DreamChunkGenerator(seed));
        SoulForestChunkGenerator.init(seed);
        PlainsChunkGenerator.init(seed);
        MudBeachChunkGenerator.init(seed);
        CloudChunkGenerator.init(seed);
        GlaciteCaveChunkGenerator.init(seed);

        creator.environment(World.Environment.NORMAL);

        World dream = creator.createWorld();

        // ** STRUCTURES POPULATORS REGISTER **
        dream.getPopulators().add(new CloudCastleStructure());
        dream.getPopulators().add(new BaseCampStructure());
        dream.getPopulators().add(new SoulAltarStructure());

        // ** POPULATORS REGISTER **
        registeredFeatures.forEach(populator -> dream.getPopulators().add(populator));

        // ** SET GAMERULE FOR THE WORLD **
        dream.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        dream.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        dream.setGameRule(GameRule.DISABLE_RAIDS, true);
        dream.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        dream.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        dream.setGameRule(GameRule.NATURAL_REGENERATION, false);

        dream.setTime(18000);

        plugin.getLogger().info("Dream Dimension ready!");
    }

    // ** STRUCTURE NBT MANAGING **
    private static void registrerFeatures(FeaturesPopulator populator) {
        registeredFeatures.add(populator);
    }

    // ** BIOME MANAGING **

    public static DreamBiome getDreamBiome(Biome biome) {
        for (DreamBiome dreamBiome : DreamBiome.values()) {
            if (!dreamBiome.getBiome().equals(biome)) continue;

            return dreamBiome;
        }

        return DreamBiome.SCULK_PLAINS;
    }

    public static DreamBiome getDreamBiome(Player player) {
        World world = player.getWorld();

        if (!world.getName().equals(DIMENSION_NAME)) return null;

        return getDreamBiome(world.getBiome(player.getLocation()));
    }

    // ** SEED MANAGING **

    private static long createSeed() {
        Random random = new Random();

        long seed = random.nextLong();

        while (seed == 0) {
            seed = random.nextLong();
        }

        return seed;
    }

    private static void loadSeed() {
        if (!seedFile.exists()) {
            OMCPlugin.getInstance().getLogger().info("Fichier seed.yml manquant, il sera créé au saveSeed().");
        }
        seedConfig = YamlConfiguration.loadConfiguration(seedFile);
    }

    private static void saveSeed(long seed) {
        seedConfig.set("world_seed", seed);
        try {
            seedConfig.save(seedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasSeedChanged() {
        long saved = seedConfig.getLong("world_seed", -1);
        World dream = Bukkit.getWorld(DIMENSION_NAME);
        if (dream == null) return false;
        long current = dream.getSeed();
        return saved != current;
    }
}

