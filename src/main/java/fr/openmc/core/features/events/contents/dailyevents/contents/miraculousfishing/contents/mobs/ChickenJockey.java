package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.mobs;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChickenJockey extends CustomMob<Chicken> {
    public ChickenJockey(String id) {
        super(id,
                "Chicken Jockey",
                Chicken.class,
                20,
                67,
                RandomUtils.randomBetween(0.4, 0.5),
                List.of(
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.TENDERS,
                                0.7, 1, 1),
                        new XpLoot(10, 30, 1)
                )
        );
    }

    @Override
    public Chicken spawn(Location spawnLocation) {
        Chicken chicken = spawnLocation.getWorld().spawn(spawnLocation, Chicken.class);

        Zombie babyZombie = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class);
        babyZombie.setBaby();
        babyZombie.getEquipment().setHelmet(ItemStack.of(Material.DIAMOND_HELMET));

        chicken.addPassenger(babyZombie);

        return chicken;
    }
}
