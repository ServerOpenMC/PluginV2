package fr.openmc.core.utils.world.region;

import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Region {

    private BukkitTask detectionTask = null;

    private final Map<Entity, Boolean> insideState = new HashMap<>();

    private Set<Chunk> chunks;

    public abstract boolean isInside(Location loc);

    public abstract World getWorld();

    public abstract Set<Chunk> getChunks();

    public void addDetection(Predicate<Entity> condition, Consumer<Entity> action) {

        if (detectionTask != null)
            return;

        detectionTask = Bukkit.getScheduler().runTaskTimer(
                OMCPlugin.getInstance(),
                () -> {

                    // get entity from chunks that are use by the region
                    for (Chunk chunk : chunks) {
                        for (Entity entity : chunk.getEntities()) {

                            boolean inside = isInside(entity.getLocation());

                            if (condition.test(entity) && inside) {
                                action.accept(entity);
                            }

                            insideState.put(entity, inside);
                        }
                    }

                },
                0L,
                10L // 0.5s
        );
    }

    public void removeDetection() {
        if (detectionTask != null) {
            detectionTask.cancel();
            detectionTask = null;
        }
        insideState.clear();
    }

    public void initChunks() {
        chunks = getChunks();
    }

    // use for enter/exit detection only
    public boolean simpleInOutVerification(Entity entity) {
        boolean wasInside = insideState.getOrDefault(entity, false);

        return !wasInside;
    }
}