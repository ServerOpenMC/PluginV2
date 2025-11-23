package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Stray;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class DreamStray extends DreamMob {

    public DreamStray() {
        super("dream_stray",
                "Stray Endormi",
                EntityType.STRAY,
                9.0,
                3L,
                0.2,
                1.2,
                List.of()
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

        applyStats(stray);

        stray.setGlowing(true);
        EntityEquipment equipment = stray.getEquipment();
        if (stray.canUseEquipmentSlot(EquipmentSlot.FEET)) {
            equipment.setBoots(DreamItemRegistry.getByName("omc_dream:cloud_boots").getBest());
            equipment.setBootsDropChance(0.0f);
        }

        return stray.createSnapshot();
    }
}