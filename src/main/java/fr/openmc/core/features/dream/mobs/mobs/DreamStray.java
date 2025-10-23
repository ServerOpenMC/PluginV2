package fr.openmc.core.features.dream.mobs.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DreamStray extends DreamMob {

    public DreamStray() {
        super("dream_stray",
                "Stray Endormi",
                EntityType.STRAY,
                9.0,
                4.0,
                0.8,
                1.0,
                List.of(),
                Biome.THE_VOID
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public EntitySnapshot createSnapshot() {
        World world = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        Breeze breeze = world.createEntity(new Location(world, 0, 0, 0), Breeze.class);

        breeze.customName(Component.text(this.getName()));
        breeze.setCustomNameVisible(true);

        this.setAttributeIfPresent(breeze, Attribute.MAX_HEALTH, this.getHealth());
        breeze.setHealth(this.getHealth());
        this.setAttributeIfPresent(breeze, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(breeze, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(breeze, Attribute.SCALE, this.getScale());

        breeze.setGlowing(true);

        breeze.getPersistentDataContainer().set(
                DreamMobManager.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return breeze.createSnapshot();
    }
}