package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.normal;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HouseZombieLV2 extends CustomMob<Zombie> {

    public HouseZombieLV2(String id) {
        super(
                id,
                TranslationManager.translation("feature.events.halloween.festival.mob.zombie.name.1"),
                Zombie.class,
                30,
                5.0,
                List.of(
                        new XpLoot(75, 125, 0.7),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.4, 2, 5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.16, 2, 5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.1, 1)
                )
        );
    }

    @Override
    public Zombie spawn(Location spawnLocation) {
        Zombie zombie = getPreBuildMob(spawnLocation);

        zombie.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
        zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));

        Material weapon = ThreadLocalRandom.current().nextBoolean()
                ? Material.IRON_AXE
                : Material.IRON_SWORD;

        zombie.getEquipment().setItemInMainHand(new ItemStack(weapon));

        return zombie;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
