package fr.openmc.core.registry.mobs;

import org.bukkit.Location;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.LivingEntity;

import java.util.function.Function;

public record CustomMobEntry(
        String id,
        Function<String, CustomMob<?>> factory
) {
    public CustomMob<?> getMob() {
        return this.factory().apply(id);
    }

    public void apply(LivingEntity entity) {
        getMob().apply(entity);
    }

    public void spawn(Location spawningLocation) {
        getMob().spawn(spawningLocation);
    }

    public EntitySnapshot getMobSnapshot() {
        return getMob().getMobSnapshot();
    }

    public EntitySnapshot getMobSnapshot(Object... objects) {
        return getMob().getMobSnapshot(objects);
    }
}
