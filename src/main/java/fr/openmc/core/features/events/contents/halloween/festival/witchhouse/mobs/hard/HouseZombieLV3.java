package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.hard;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HouseZombieLV3 extends CustomMob<Zombie> {

    public HouseZombieLV3(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.zombie.name.2"),
                Zombie.class,
                40,
                7.0,
                List.of(
                        new XpLoot(120, 200, 0.85),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.5, 3, 7),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 0.3, 1, 5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.01, 1, 2),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_RED, 0.002, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW, 0.0005, 1)
                )
        );
    }

    @Override
    public Zombie spawn(Location spawnLocation) {
        Zombie zombie = getPreBuildMob(spawnLocation);

        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setItemInMainHand(
                new ItemStack(RandomUtils.getRandomObject(
                        Material.IRON_AXE,
                        Material.IRON_SWORD
                ))
        );

        return zombie;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
