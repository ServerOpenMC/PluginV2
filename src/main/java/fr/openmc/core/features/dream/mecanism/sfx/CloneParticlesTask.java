package fr.openmc.core.features.dream.mecanism.sfx;

import fr.openmc.core.utils.bukkit.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class CloneParticlesTask extends BukkitRunnable {

    private final Location sleepingLocation;
    public CloneParticlesTask(Location sleepingLocation) {
        this.sleepingLocation = sleepingLocation;
    }
    @Override
    public void run() {
        ParticleUtils.spawnConvergingParticles(sleepingLocation, 15);
    }
}
