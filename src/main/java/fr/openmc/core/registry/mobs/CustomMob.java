package fr.openmc.core.registry.mobs;

import fr.openmc.core.registry.loottable.CustomLoot;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
public abstract class CustomMob<T extends LivingEntity> {
    private final String id;
    private final String name;
    private final Class<T> entityClass;
    private final Set<CustomMobAttribute> baseAttributes = new HashSet<>();
    private final List<CustomLoot> loots = new ArrayList<>();

    public CustomMob(String id, String name, Class<T> entityClass, double health, double damage, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
    }

    public CustomMob(String id, String name, Class<T> entityClass, double health, double damage, double speed, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MOVEMENT_SPEED, speed));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
    }

    public CustomMob(String id, String name, Class<T> entityClass, double health, double damage, double speed, List<CustomLoot> loots, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MOVEMENT_SPEED, speed));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.loots.addAll(loots);
    }

    public CustomMob(String id, String name, Class<T> entityClass, double health, double damage, List<CustomLoot> loots, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.loots.addAll(loots);
    }

    // * peut etre Override
    public T spawn(Location location) {
        return null;
    }

    // * peut etre Override
    public void apply(LivingEntity livingEntity) {
        applyStats(livingEntity);
    }

    // * peut etre Override
    public EntitySnapshot getMobSnapshot() {
        World world = Bukkit.getWorld("world");
        if (world == null) return null;
        LivingEntity entity = world.createEntity(new Location(world, 0, 0, 0), entityClass);

        applyStats(entity);

        return entity.createSnapshot();
    }

    // * peut etre Override
    public EntitySnapshot getMobSnapshot(Object... objects) {
        return null;
    }

    // * peut etre Override
    public T getPreBuildMob(Location spawnLocation) {
        T livingEntity = spawnLocation.getWorld().spawn(spawnLocation.add(0, 1, 0), entityClass, null, CreatureSpawnEvent.SpawnReason.CUSTOM);
        applyStats(livingEntity);
        return livingEntity;
    }

    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {}

    public void applyStats(LivingEntity livingEntity) {
        livingEntity.customName(Component.text(this.getName()));
        livingEntity.setCustomNameVisible(true);

        for (CustomMobAttribute attribute : baseAttributes) {
            attribute.setAttributeIfPresent(livingEntity);
        }

        livingEntity.setHealth(this.baseAttributes.stream()
                .filter(attr -> attr.attribute() == Attribute.MAX_HEALTH)
                .findFirst()
                .map(CustomMobAttribute::value)
                .orElse(livingEntity.getHealth())
        );

        livingEntity.getPersistentDataContainer().set(
                CustomMobRegistry.CUSTOM_MOB_KEY,
                PersistentDataType.STRING,
                this.getId()
        );
    }

    public double getHealth() {
        return baseAttributes.stream()
                .filter(attr -> attr.attribute() == Attribute.MAX_HEALTH)
                .findFirst()
                .map(CustomMobAttribute::value)
                .orElse(20.0);
    }
}
