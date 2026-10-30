package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.hard;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HouseBruteLV3 extends CustomMob<PiglinBrute> {

    public HouseBruteLV3(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.brute.name.2"),
                PiglinBrute.class,
                50,
                8.0,
                List.of(
                        new XpLoot(200, 350, 1.0),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.8, 4, 10),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 0.3, 1, 5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.01, 1, 2),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_RED, 0.002, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW, 0.0005, 1)
                )
        );
    }

    @Override
    public PiglinBrute spawn(Location spawnLocation) {
        PiglinBrute zombie = getPreBuildMob(spawnLocation);

        zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));

        zombie.setAdult();

        return zombie;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
