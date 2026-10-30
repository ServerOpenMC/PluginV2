package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.normal;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class HouseBlazeLV2 extends CustomMob<Blaze> {

    public HouseBlazeLV2(String id) {
        super(
                id,
                TranslationManager.translation(
                        "feature.events.halloween.festival.mob.blaze.name.1"
                ),
                Blaze.class,
                25,
                6.0,
                List.of(
                        new XpLoot(75, 110, 0.7),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN,
                                0.55,
                                1,
                                4
                        ),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN,
                                0.25,
                                1,
                                4
                        ),
                        new ItemLoot(
                                OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE,
                                0.1,
                                1
                        )
                )
        );
    }

    @Override
    public Blaze spawn(Location spawnLocation) {
        return getPreBuildMob(spawnLocation);
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
