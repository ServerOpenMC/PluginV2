package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.easy;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HouseZombieLV1 extends CustomMob<Zombie> {

    public HouseZombieLV1(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.zombie.name.0"),
                Zombie.class,
                20,
                3.0	,
                List.of(
                        new XpLoot(50, 75, 0.5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.3, 1, 4)
                )
        );
    }

    @Override
    public Zombie spawn(Location spawnLocation) {
        Zombie zombie = this.getPreBuildMob(spawnLocation);

        zombie.getEquipment().setHelmet(new ItemStack(
                RandomUtils.getRandomObject(
                        Material.PUMPKIN,
                        Material.AIR
                )
        ));

        zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));

        zombie.getEquipment().setBoots(new ItemStack(
                RandomUtils.getRandomObject(
                        Material.LEATHER_BOOTS,
                        Material.COPPER_BOOTS
                )
        ));

        zombie.getEquipment().setItemInMainHand(new ItemStack(
                RandomUtils.getRandomObject(
                        Material.IRON_AXE,
                        Material.IRON_SHOVEL,
                        Material.AIR
                )
        ));

        return zombie;
    }
}
