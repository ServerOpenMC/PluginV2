package fr.openmc.core.features.dream.fishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import fr.openmc.core.features.dream.mobs.DreamLoot;
import fr.openmc.core.utils.ParticleUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CloudFishingManager {
    @Getter
    private static final HashMap<UUID, FishBiteTask> hookedPlayers = new HashMap<>();

    public static final double Y_CLOUD_FISHING = CloudChunkGenerator.MIN_HEIGHT_CLOUD - 5;
    private static final Set<DreamLoot> FISHING_LOOTS = Set.of(
            new DreamLoot(
                    DreamItemRegister.getByName("omc_dream:poissonion"),
                    0.5,
                    1,
                    2
            ),
            new DreamLoot(
                    DreamItemRegister.getByName("omc_dream:moon_fish"),
                    0.5,
                    1,
                    2
            ),
            new DreamLoot(
                    DreamItemRegister.getByName("omc_dream:sun_fish"),
                    0.5,
                    1,
                    2
            ),
            new DreamLoot(
                    DreamItemRegister.getByName("omc_dream:dockerfish"),
                    0.2,
                    1,
                    1
            ),
            new DreamLoot(
                    DreamItemRegister.getByName("omc_dream:somnifere"),
                    0.4,
                    1,
                    1
            )
    );

    public static void init() {
        OMCPlugin.registerEvents(
                new PlayerFishListener()
        );
    }

    public static void simulateDreamFishing(Player player, FishHook hook) {
        World world = hook.getWorld();
        Random random = new Random();

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

        player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 1f, 1f);

        hook.getWorld().spawnParticle(Particle.DRAGON_BREATH, hook.getLocation().add(0, 1, 0), 35, 0.3, 0.2, 0.3, 0.1);

        hookedPlayers.put(player.getUniqueId(), new FishBiteTask(player, hook, 30L));
    }

    public static List<ItemStack> rollFishingLoots() {
        List<ItemStack> result = new ArrayList<>();

        double totalChance = FISHING_LOOTS.stream()
                .mapToDouble(DreamLoot::chance)
                .sum();

        double roll = Math.random() * totalChance;
        double sumChance = 0.0;

        for (DreamLoot loot : FISHING_LOOTS) {
            sumChance += loot.chance();
            if (roll <= sumChance) {
                ItemStack item = loot.item().getBest();
                item.setAmount(loot.getRandomAmount());
                result.add(item);
                break;
            }
        }

        if (result.isEmpty()) {
            DreamLoot next = FISHING_LOOTS.iterator().next();
            ItemStack item = next.item().getBest();
            item.setAmount(next.getRandomAmount());
            result.add(item);
        }

        return result;
    }
}
