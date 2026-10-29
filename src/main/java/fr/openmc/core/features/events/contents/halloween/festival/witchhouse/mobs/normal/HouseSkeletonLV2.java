package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.normal;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HouseSkeletonLV2 extends CustomMob<Skeleton> {

    public HouseSkeletonLV2(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.skeleton.name.1"),
                Skeleton.class,
                24,
                4.0,
                List.of(
                        new XpLoot(75, 110, 0.7),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.35, 1, 4),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 0.10, 1, 4),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.04, 1)
                )
        );
    }

    @Override
    public Skeleton spawn(Location spawnLocation) {
        Skeleton skeleton = getPreBuildMob(spawnLocation);

        skeleton.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        skeleton.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));

        return skeleton;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
