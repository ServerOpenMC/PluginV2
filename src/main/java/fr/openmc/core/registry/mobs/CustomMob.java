package fr.openmc.core.registry.mobs;

import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public abstract class CustomMob<T extends LivingEntity> {
    private final String id;
    private final Component name;
    private final Class<T> entityClass;
    private final Set<CustomMobAttribute> baseAttributes = new HashSet<>();
    private CustomLootTable lootTable = null;

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
    }

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, double speed, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MOVEMENT_SPEED, speed));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
    }

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, double speed, CustomLootTable lootTable, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MOVEMENT_SPEED, speed));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.lootTable = lootTable;
    }

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, CustomLootTable lootTable, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.lootTable = lootTable;
    }

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, double speed, List<CustomLoot> loots, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MOVEMENT_SPEED, speed));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.lootTable = getPreBuildLootTable(loots);
    }

    public CustomMob(String id, Component name, Class<T> entityClass, double health, double damage, List<CustomLoot> loots, CustomMobAttribute... baseAttributes) {
        this.id = id;
        this.name = name;
        this.entityClass = entityClass;
        this.baseAttributes.add(new CustomMobAttribute(Attribute.MAX_HEALTH, health));
        this.baseAttributes.add(new CustomMobAttribute(Attribute.ATTACK_DAMAGE, damage));
        this.baseAttributes.addAll(Arrays.stream(baseAttributes).toList());
        this.lootTable = getPreBuildLootTable(loots);
    }

    // * peut etre Override
    public T spawn(Location location) {
        return null;
    }

    // * peut etre Override
    public void apply(T entity) {
        applyStats(entity);
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

        CustomMobRegistry.HAS_BOSSBAR.add(livingEntity.getUniqueId());
        return livingEntity;
    }

    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {}

    public void applyStats(LivingEntity livingEntity) {
        livingEntity.customName(this.getName());
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

        registerAsCustomMob(livingEntity);
    }

    public double getHealth() {
        return baseAttributes.stream()
                .filter(attr -> attr.attribute() == Attribute.MAX_HEALTH)
                .findFirst()
                .map(CustomMobAttribute::value)
                .orElse(20.0);
    }

    public void registerAsCustomMob(LivingEntity entity) {
        entity.getPersistentDataContainer().set(
                CustomMobRegistry.CUSTOM_MOB_KEY,
                PersistentDataType.STRING,
                this.getId()
        );
    }

    public void unregisterAsCustomMob(LivingEntity entity) {
        entity.getPersistentDataContainer().remove(CustomMobRegistry.CUSTOM_MOB_KEY);
    }

    private CustomLootTable getPreBuildLootTable(List<CustomLoot> lootList) {
        return new CustomLootTable() {
            @Override
            public Component getName() {
                return Component.empty();
            }

            @Override
            public String getNamespace() {
                return "omc_mobs:mob";
            }

            @Override
            public Set<CustomLoot> getLoots() {
                return new HashSet<>(lootList);
            }
        };
    }
}
