package fr.openmc.core.features.singularity.sub.worldsfx.sfx;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PulseSingularitySFX {

    private BukkitTask currentTask;
    private final Location origin;

    private boolean isConverging = true;
    private final long convergeInterval = 20 * 3L; // 3 sec

    public PulseSingularitySFX(Location origin) {
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
        currentTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::pulse, delay);
    }

    private void pulse() {
        long nextInverval = getNextInterval();
        if (!origin.getWorld().getPlayers().isEmpty())
            if (isConverging) {
                ParticleUtils.spawnConvergingParticlesSpherical(origin, Particle.GLOW_SQUID_INK, 250, 100.0, (int) nextInverval + 40, null);
            } else {
                ParticleUtils.spawnRepulsedParticlesSpherical(origin, Particle.FLASH, 400, 120, (int) nextInverval + 40, Color.fromRGB(93, 217, 210));
            }

        isConverging = !isConverging;

        scheduleNextPulse(nextInverval);
    }

    private long getNextInterval() {
        LocalDateTime now = LocalDateTime.now();

        double nextImpulsion = now.until(ImpulsionSingularitySFX.getNextImpulsion(), ChronoUnit.SECONDS);
        double impulsionInterval = ImpulsionSingularitySFX.IMPULSION_INTERVAL * 60;

        double ratio = Math.clamp(nextImpulsion / impulsionInterval, 0.5, 1.0);

        return (long) (convergeInterval * ratio);
    }
}
