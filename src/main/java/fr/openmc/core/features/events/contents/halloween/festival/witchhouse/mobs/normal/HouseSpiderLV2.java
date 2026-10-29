package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.normal;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.CaveSpider;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class HouseSpiderLV2 extends CustomMob<CaveSpider> {

    public HouseSpiderLV2(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.spider.name.1"),
                CaveSpider.class,
                18,
                3.0,
                List.of(
                        new XpLoot(60, 90, 0.65),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.3, 1, 3),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 0.07, 1, 4),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.02, 1)
                )
        );
    }

    @Override
    public CaveSpider spawn(Location spawnLocation) {
        return getPreBuildMob(spawnLocation);
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
