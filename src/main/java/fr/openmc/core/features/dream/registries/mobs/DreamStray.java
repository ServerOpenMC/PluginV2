package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Stray;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class DreamStray extends DreamMob<Stray> {

    public DreamStray(String id) {
        super(id,
                "Stray Endormi",
                Stray.class,
                9.0,
                3L,
                0.2,
                1.2
        );
    }

    @Override
    public EntitySnapshot getMobSnapshot() {
        World world = DreamDimensionManager.DREAM_WORLD;
        if (world == null) return null;
        LivingEntity stray = world.createEntity(new Location(world, 0, 0, 0), Stray.class);

        applyStats(stray);

        stray.setGlowing(true);
        EntityEquipment equipment = stray.getEquipment();
        if (stray.canUseEquipmentSlot(EquipmentSlot.FEET)) {
            equipment.setBoots(DreamItemRegistry.CLOUD_BOOTS.getBest());
            equipment.setBootsDropChance(0.0f);
        }

        return stray.createSnapshot();
    }
}