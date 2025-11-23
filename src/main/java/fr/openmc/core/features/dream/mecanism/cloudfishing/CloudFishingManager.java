package fr.openmc.core.features.dream.mecanism.cloudfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.models.registry.loottable.DreamLootTable;
import fr.openmc.core.features.dream.registries.DreamLootTableRegistry;
import fr.openmc.core.utils.ParticleUtils;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CloudFishingManager {
    @Getter
    private static final HashMap<UUID, FishBiteTask> hookedPlayers = new HashMap<>();

    public static final double Y_CLOUD_FISHING = CloudChunkGenerator.MIN_HEIGHT_CLOUD - 5;
    public static final DreamLootTable FISHING_LOOT_TABLE = DreamLootTableRegistry.getByKey(Key.key("dream:cloud_fishing"));

    public static void init() {
        OMCPlugin.registerEvents(
                new PlayerFishListener()
        );
    }

    public static void simulateDreamFishing(Player player, FishHook hook) {
        World world = hook.getWorld();
        Random random = ThreadLocalRandom.current();

        Location hookLoc = hook.getLocation().clone();

        Location start = hookLoc.clone().add(random.nextInt(6) - 3, -random.nextDouble(2), random.nextInt(6) - 3);

        int steps = 40;
        int[] counter = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!hook.isValid() || !player.isOnline()) {
                    cancel();
                    return;
                }

                double t = counter[0] / (double) steps;
                double x = start.getX() + (hookLoc.getX() - start.getX()) * t;
                double y = start.getY() + (hookLoc.getY() - start.getY()) * t;
                double z = start.getZ() + (hookLoc.getZ() - start.getZ()) * t;

                Location point = new Location(world, x, y, z);
                ParticleUtils.sendParticlePacket(player, point.add(0, 1, 0), Particle.WITCH, 3, 0.1, 0.1, 0.1, 0.01, null);

                ParticleUtils.spawnParticleCloud(player, point, Particle.CLOUD, 65, 5, 1.5);

                counter[0]++;

                if (counter[0] >= steps) {
                    cancel();
                    onFishBite(player, hook);
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 3L);
    }

    private static void onFishBite(Player player, FishHook hook) {
        if (!hook.isValid() || !player.isOnline()) return;

        player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 0.6F, 1F);

        ParticleUtils.sendParticlePacket(
                player,
                hook.getLocation().add(0, 1, 0),
                Particle.DRAGON_BREATH,
                35,
                0.3D,
                0.2D,
                0.3D,
                0.1D,
                (Float) 1.0f
        );

        hookedPlayers.put(player.getUniqueId(), new FishBiteTask(player, hook, 30L));
    }
}
