package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Stray;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DreamStray extends DreamMob {

    public DreamStray() {
        super("dream_stray",
                "Stray Endormi",
                EntityType.STRAY,
                13.0,
                4.0,
                0.3,
                1.2,
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
        if (world == null) return null;
        Stray stray = world.createEntity(new Location(world, 0, 0, 0), Stray.class);

        stray.customName(Component.text(this.getName()));
        stray.setCustomNameVisible(true);

        this.setAttributeIfPresent(stray, Attribute.MAX_HEALTH, this.getHealth());
        stray.setHealth(this.getHealth());
        this.setAttributeIfPresent(stray, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(stray, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(stray, Attribute.SCALE, this.getScale());

        stray.setGlowing(true);
        EntityEquipment equipment = stray.getEquipment();
        if (stray.canUseEquipmentSlot(EquipmentSlot.FEET)) {
            equipment.setBoots(DreamItemRegistry.getByName("omc_dream:cloud_boots").getBest());
            equipment.setBootsDropChance(0.0f);
        }

        stray.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return stray.createSnapshot();
    }
}