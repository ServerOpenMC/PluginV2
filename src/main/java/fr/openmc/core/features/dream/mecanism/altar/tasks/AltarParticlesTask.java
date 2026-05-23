package fr.openmc.core.features.dream.mecanism.altar.tasks;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.registry.DreamBlock;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AltarParticlesTask extends BukkitRunnable {

    private static final List<Vector> CIRCLE_PARTICLE = new ArrayList<>();

    static {
        for (int i = 0; i < 360; i += 20) {
            double radians = Math.toRadians(i);
            CIRCLE_PARTICLE.add(new Vector(
                    Math.cos(radians) * 0.7,
                    0,
                    Math.sin(radians) * 0.7
            ));
        }
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!DreamUtils.isInDreamWorld(player)) continue;
            Location playerLoc = player.getLocation();
            World world = playerLoc.getWorld();

            List<DreamBlock> altars = DreamBlocksRegistry.getDreamBlocksByType("altar");

            for (DreamBlock altar : altars) {
                if (altar.location().getWorld() != world) continue;

                Location center = altar.location();

                if (center.distanceSquared(playerLoc) > 30 * 30) continue;

                spawnCircleParticles(center);
            }
        }
    }

    private void spawnCircleParticles(Location base) {
        World world = base.getWorld();
        double x = base.getX() + 0.5;
        double y = base.getY() + 1.5;
        double z = base.getZ() + 0.5;

        for (Vector vec : CIRCLE_PARTICLE) {
            world.spawnParticle(
                    Particle.DUST,
                    x + vec.getX(),
                    y,
                    z + vec.getZ(),
                    1,
                    new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
            );
        }
    }
}
