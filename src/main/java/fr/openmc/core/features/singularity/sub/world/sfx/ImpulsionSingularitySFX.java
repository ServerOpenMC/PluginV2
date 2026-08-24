package fr.openmc.core.features.singularity.sub.world.sfx;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;

public class ImpulsionSingularitySFX {
    public static final long IMPULSION_INTERVAL = 10L; // 10 minutes
    public static LocalDateTime lastImpulsion = LocalDateTime.now();

    private BukkitTask currentTask;
    private final Location origin;

    public ImpulsionSingularitySFX(Location origin) {
        this.origin = origin;
    }

    public void start() {
        currentTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            lastImpulsion = LocalDateTime.now();

            if (!origin.getWorld().getPlayers().isEmpty()) {
                origin.getWorld().playSound(origin, Sound.ENTITY_WARDEN_SONIC_BOOM, 156.0f, 0.1f);
                origin.getWorld().playSound(origin, Sound.BLOCK_BEACON_POWER_SELECT, 156.0f, 0.1f);
                ParticleUtils.spawnRepulsedParticlesSpherical(origin, Particle.SNEEZE, 500, 400, 400, 20 * 50, null);
            }
            }, 0L, IMPULSION_INTERVAL * 60 * 20);
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public static LocalDateTime getNextImpulsion() {
        return lastImpulsion.plusMinutes(IMPULSION_INTERVAL);
    }
}
