package fr.openmc.core.features.dream.mobs.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DreamPhantom extends DreamMob {

    public DreamPhantom() {
        super("dream_phantom",
                "Phantom Réveillé",
                EntityType.STRAY,
                9.0,
                3.0,
                0.4,
                RandomUtils.randomBetween(0.3, 0.6),
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
        Phantom phantom = world.createEntity(new Location(world, 0, 0, 0), Phantom.class);

        phantom.customName(Component.text(this.getName()));
        phantom.setCustomNameVisible(true);

        this.setAttributeIfPresent(phantom, Attribute.MAX_HEALTH, this.getHealth());
        phantom.setHealth(this.getHealth());
        this.setAttributeIfPresent(phantom, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(phantom, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(phantom, Attribute.SCALE, this.getScale());

        phantom.setGlowing(true);

        phantom.getPersistentDataContainer().set(
                DreamMobManager.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return phantom.createSnapshot();
    }
}