package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.DreamBlock;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AltarParticlesTask extends BukkitRunnable {

    @Override
    public void run() {
        List<DreamBlock> altars = DreamBlocksRegistry.getDreamBlocksByType("altar");
        if (altars.isEmpty()) return;

        for (DreamBlock altar : altars) {
            Location center = altar.location().clone().add(0.5, 1.5, 0.5);

            boolean nearby = false;
            for (Player player : center.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(center) <= 30 * 30) {
                    nearby = true;
                    break;
                }
            }

            if (!nearby) continue;

            for (int i = 0; i < 360; i += 20) {
                double radians = Math.toRadians(i);
                double x = Math.cos(radians) * 0.7;
                double z = Math.sin(radians) * 0.7;

                center.getWorld().spawnParticle(
                        Particle.DUST,
                        center.clone().add(x, 0, z),
                        1,
                        new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
                );
            }
        }
    }
}
