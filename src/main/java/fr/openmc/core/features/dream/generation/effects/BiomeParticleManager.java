package fr.openmc.core.features.dream.generation.effects;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeParticleManager {

    private static final Map<UUID, BukkitTask> runningTasks = new HashMap<>();

    // ** PARTICLES FOR EACH ZONES **
    private static final Map<Biome, Particle> biomeParticles = new HashMap<>() {
        {
            put(DreamBiome.SOUL_FOREST.getBiome(), Particle.SCULK_SOUL);
            put(DreamBiome.SCULK_PLAINS.getBiome(), Particle.TRIAL_SPAWNER_DETECTION_OMINOUS);
            put(DreamBiome.MUD_BEACH.getBiome(), Particle.ASH);
            put(DreamBiome.GLACITE_GROTTO.getBiome(), Particle.SNOWFLAKE);
            put(DreamBiome.CLOUD_LAND.getBiome(), Particle.SMALL_GUST);
        }
    };

    // ** CONST **
    private static final int PARTICLE_RADIUS = 8;

    public static void startTask(Player player) {
        if (runningTasks.containsKey(player.getUniqueId())) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!player.isOnline()) {
                stopTask(player);
                return;
            }

            if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) {
                stopTask(player);
                return;
            }

            Biome biome = player.getLocation().getBlock().getBiome();

            if (biomeParticles.containsKey(biome)) {
                ParticleUtils.sendRandomCubeParticles(player, biomeParticles.get(biome), PARTICLE_RADIUS, 50);
                return;
            }

            stopTask(player);
        }, 0L, 20L);

        runningTasks.put(player.getUniqueId(), task);
    }

    public static void stopTask(Player player) {
        BukkitTask task = runningTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }
}
