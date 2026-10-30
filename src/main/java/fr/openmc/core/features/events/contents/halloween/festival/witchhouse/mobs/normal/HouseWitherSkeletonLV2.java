package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.normal;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HouseWitherSkeletonLV2 extends CustomMob<WitherSkeleton> {

    public HouseWitherSkeletonLV2(String id) {
        super(
                id,
                TranslationManager.translation(
                        "feature.events.halloween.festival.mob.wither_skeleton.name.1"
                ),
                WitherSkeleton.class,
                30,
                6.0,
                List.of(
                        new XpLoot(90, 130, 0.75),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN,
                                0.4,
                                1,
                                4
                        ),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN,
                                0.13,
                                1,
                                2
                        ),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE,
                                0.04,
                                1
                        )
                )
        );
    }

    @Override
    public WitherSkeleton spawn(Location spawnLocation) {
        WitherSkeleton skeleton = getPreBuildMob(spawnLocation);

        skeleton.getEquipment().setHelmet(
                new ItemStack(Material.CHAINMAIL_HELMET)
        );

        skeleton.getEquipment().setChestplate(
                new ItemStack(Material.IRON_CHESTPLATE)
        );

        skeleton.getEquipment().setItemInMainHand(
                new ItemStack(Material.STONE_SWORD)
        );

        return skeleton;
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
