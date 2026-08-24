package fr.openmc.core.features.singularity.sub.world.sfx;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

public class InstabilitySingularitySFX {
    private BukkitTask currentTask;
    private final Location origin;

    private final int MIN_INTERVAL = 1; // en secondes
    private final int MAX_INTERVAL = 4; // en secondes

    public InstabilitySingularitySFX(Location origin) {
        this.origin = origin;
    }

    public void start() {
        scheduleNextPulse(0);
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    private void scheduleNextPulse(long delay) {
        currentTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (!origin.getWorld().getPlayers().isEmpty())
                ParticleUtils.spawnParticlesInCube(
                        origin, Particle.FLASH, 40, 20,
                        Color.fromRGB(92, 250, 235));

            long nextDelay = RandomUtils.randomBetween(MIN_INTERVAL, MAX_INTERVAL) * 20L;
            scheduleNextPulse(nextDelay);
        }, delay);
    }
}
