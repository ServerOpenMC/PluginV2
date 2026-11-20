package fr.openmc.core.features.events.contents.dailyevents;

import fr.openmc.core.bootstrap.registries.KeyedRegistry;
import fr.openmc.core.bootstrap.registries.Registry;
import fr.openmc.core.features.events.contents.christmas.mecanism.dailyevent.BlizzardEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;

public class DailyEventsRegistry extends Registry<String, DailyEvent>
        implements KeyedRegistry<String, DailyEvent>  {

    public final DailyEvent MIRACULOUS_FISHING = register(new MiraculousFishingEvent());
    public final DailyEvent GOLDEN_HARVEST = register(new GoldenHarvestEvent());
    public final DailyEvent BLOODY_NIGHT = register(new BloodyNightEvent());
    public final DailyEvent BLIZZARD = register(new BlizzardEvent());

    @Override
    public String key(DailyEvent registryObject) {
        return registryObject.getEventId();
    }
}
