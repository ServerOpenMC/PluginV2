package fr.openmc.core.features.dream.models.registry;

import fr.openmc.core.registry.loottable.CustomLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.registry.mobs.CustomMobAttribute;
import lombok.Getter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.List;

@Getter
public abstract class DreamMob<T extends LivingEntity> extends CustomMob<T> {
    private final Long damageTime;

    public DreamMob(String id, String name, Class<T> entityClass, double health, Long damageTime, double speed, double scale) {
        super(id, name, entityClass, health, 0f, speed,
                new CustomMobAttribute(Attribute.SCALE, scale));
        this.damageTime = damageTime;
    }

    public DreamMob(String id, String name, Class<T> entityClass, double health, Long damageTime, double speed, double scale, List<CustomLoot> loots) {
        super(id, name, entityClass, health, 0f, speed, loots,
                new CustomMobAttribute(Attribute.SCALE, scale));
        this.damageTime = damageTime;
    }

    public double getScale() {
        return this.getBaseAttributes().stream()
                .filter(attr -> attr.attribute() == Attribute.SCALE)
                .findFirst()
                .map(CustomMobAttribute::value)
                .orElse(1.0);
    }

    public double getSpeed() {
        return this.getBaseAttributes().stream()
                .filter(attr -> attr.attribute() == Attribute.MOVEMENT_SPEED)
                .findFirst()
                .map(CustomMobAttribute::value)
                .orElse(1.0);
    }
}
