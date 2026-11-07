package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.mobs.hard;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.registry.loottable.loots.XpLoot;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class HouseWitchLV3 extends CustomMob<Witch> {

    public HouseWitchLV3(String id) {
        super(id,
                TranslationManager.translation("feature.events.halloween.festival.mob.witch.name.2"),
                Witch.class,
                32,
                6.0,
                List.of(
                        new XpLoot(150, 250, 0.9),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BROWN, 0.9, 4, 10),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_GREEN, 0.4, 1, 5),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_BLUE, 0.1, 1, 2),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_RED, 0.02, 1),
                        new ItemLoot(OMCRegistry.CUSTOM_ITEMS.CANDY_YELLOW, 0.05, 1)
                )
        );
    }

    @Override
    public Witch spawn(Location spawnLocation) {
        return getPreBuildMob(spawnLocation);
    }

    @Override
    public void onDeath(CustomMob<?> thisMob, EntityDeathEvent event) {
        if (TerrorHouseManager.entityCount > 0)
            TerrorHouseManager.entityCount -= 1;
    }
}
