package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

/**
 * Ring ding ding daa baa
 * Baa aramba baa bom baa barooumba
 * Wh-wha-what′s going on-on?
 * Ding, ding
 * This is the Crazy Frog
 * Ding, ding
 * Bem bem!
 */
public class CrazyFrog extends DreamMob<Frog> implements Listener {

    private static final HashMap<LivingEntity, BukkitTask> jumpTasks = new HashMap<>();

    public CrazyFrog(String id) {
        super(id,
                "Grenouille Folle",
                Frog.class,
                18.0,
                0L,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(3, 2.3),
                List.of(new CustomLoot(
                        DreamItemRegistry.METAL_DETECTOR,
                        0.5,
                        1,
                        1
                ))
        );
    }

    @Override
    public Frog spawn(Location location) {
        Frog frog = this.getPreBuildMob(location);

        frog.setVariant(Frog.Variant.WARM);
        frog.setVelocity(location.getDirection().multiply(1.3));

        return frog;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(OMCRegistry.CUSTOM_MOBS.getMob(event.getEntity()) instanceof CrazyFrog)) return;
        if (!(event.getEntity() instanceof Frog frog)) return;

        if (jumpTasks.containsKey(frog)) {
            cancelJumps(frog);
        }

        jumpTasks.put(frog, Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                jump(frog, RandomUtils.randomBetween(14, 15));
                for (int i = 0; i <= 3; i++) {
                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        jump(frog, RandomUtils.randomBetween(9, 13));
                    }, 40L * i);
                }},5L));
    }

    private void jump(LivingEntity entity, double distance) {
        Location to = LocationUtils.randomLocation(entity.getLocation(), distance);
        Location from = entity.getLocation();

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        double vy = 0.85;
        double tick = (vy / 0.08) / 2;
        double vx = dx / tick;
        double vz = dz / tick;

        entity.setVelocity(new Vector(vx, vy, vz));
    }

    private void cancelJumps(Frog frog) {
        BukkitTask task = jumpTasks.remove(frog);
        if (task != null) task.cancel();
    }
}
