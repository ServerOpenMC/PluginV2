package fr.openmc.core.features.cube;

import fr.openmc.core.features.cube.multiblocks.MultiBlockManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ReproductionTask extends BukkitRunnable {
    private final Cube parent;
    private final Cube babyCube;
    private final Location parentCenter;
    private final Location babyOrigin;
    private final int babySize;
    private final World world;
    private final int totalTicks;
    private final int interval;
    private int elapsed = 0;

    public ReproductionTask(Cube parent, Cube babyCube, Location parentCenter,
                            Location babyOrigin, int babySize, World world,
                            int totalTicks, int interval) {
        this.parent = parent;
        this.babyCube = babyCube;
        this.parentCenter = parentCenter;
        this.babyOrigin = babyOrigin;
        this.babySize = babySize;
        this.world = world;
        this.totalTicks = totalTicks;
        this.interval = interval;
    }

    @Override
    public void run() {
        if (elapsed >= totalTicks) {
            babyCube.build();

            world.playSound(babyOrigin, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.3f, 0.2f);
            world.spawnParticle(Particle.EXPLOSION_EMITTER, babyCube.getCenter(), 3, 1, 1, 1, 0);

            MultiBlockManager.register(babyCube);
            cancel();
            parent.reproductionTask = null;
            return;
        }

        drawParticleLink(world, parentCenter, babyCube.getCenter());
        drawParticleCube(world, babyOrigin, babySize);

        elapsed += interval;
    }

    public void forceReproduction() {
        this.elapsed = totalTicks;
    }

    private void drawParticleLink(World world, Location from, Location to) {
        Vector dir = to.clone().subtract(from).toVector();
        int points = 40;
        for (int i = 0; i <= points; i++) {
            Location point = from.clone().add(dir.clone().multiply(i / (double) points));
            world.spawnParticle(Particle.SNEEZE, point, 1, 0, 0, 0, 0);
        }
    }

    private void drawParticleCube(World world, Location origin, int size) {
        for (int x = 0; x <= size; x++) {
            for (int y = 0; y <= size; y++) {
                for (int z = 0; z <= size; z++) {
                    if (x == 0 || x == size || y == 0 || y == size || z == 0 || z == size) {
                        Location loc = origin.clone().add(x, y, z);
                        world.spawnParticle(Particle.PORTAL, loc, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
