package fr.openmc.core.registry.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
        ((CustomMob<LivingEntity>) getMob()).apply(entity);
    }

    public Entity spawn(Location spawningLocation) {
        return getMob().spawn(spawningLocation);
    }

    public EntitySnapshot getMobSnapshot() {
        return getMob().getMobSnapshot();
    }

    public EntitySnapshot getMobSnapshot(Object... objects) {
        return getMob().getMobSnapshot(objects);
    }
}
