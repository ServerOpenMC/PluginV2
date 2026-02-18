package fr.openmc.core.features.dream.models.registry;

import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.registry.loottable.CustomLoot;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Getter
public abstract class DreamMob {
    private final String id;
    private final String name;
    private final EntityType type;
    private final double health;
    private final Long damageTime;
    private final double speed;
    private final double scale;
    private final List<CustomLoot> loots;

    public DreamMob(String id, String name, EntityType type, double health, Long damageTime, double speed, double scale, List<CustomLoot> loots) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.health = health;
        this.damageTime = damageTime;
        this.speed = speed;
        this.scale = scale;
        this.loots = loots;
    }

    public abstract LivingEntity spawn(Location location);

    protected void setAttributeIfPresent(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }

    public DreamMob getMob() {
        return this;
    }

    public LivingEntity getPreBuildMob(Location spawnLocation) {
        LivingEntity livingEntity = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation.add(0, 1, 0), this.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        applyStats(livingEntity);

        return livingEntity;
    }

    public void applyStats(LivingEntity livingEntity) {
        livingEntity.customName(Component.text(this.getName()));
        livingEntity.setCustomNameVisible(true);

        this.setAttributeIfPresent(livingEntity, Attribute.MAX_HEALTH, this.getHealth());
        livingEntity.setHealth(this.getHealth());


        this.setAttributeIfPresent(livingEntity, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(livingEntity, Attribute.SCALE, this.getScale());

        livingEntity.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );
    }
}
