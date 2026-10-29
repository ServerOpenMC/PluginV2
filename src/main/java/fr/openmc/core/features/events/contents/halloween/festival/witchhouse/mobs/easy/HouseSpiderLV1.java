package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.easy;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Spider;

import java.util.List;

public class HouseSpiderLV1 extends CustomMob<Spider> {

    public HouseSpiderLV1(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.spider.name.0"),
                Spider.class,
                16,
                3.0,
                List.of(
                        new XpLoot(25, 50, 0.5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.2, 1, 2)
                )
        );
    }

    @Override
    public Spider spawn(Location spawnLocation) {
        return getPreBuildMob(spawnLocation);
    }
}
