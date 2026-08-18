package fr.openmc.core.features.events.contents.dailyevents;

import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;

public class DailyEventsRegistry extends Registry<String, DailyEvent>
        implements KeyedRegistry<String, DailyEvent>  {

    public final DailyEvent MIRACULOUS_FISHING = register(new MiraculousFishingEvent());
    public final DailyEvent GOLDEN_HARVEST = register(new GoldenHarvestEvent());
    public final DailyEvent BLOODY_NIGHT = register(new BloodyNightEvent());

    @Override
    public String key(DailyEvent registryObject) {
        return registryObject.getEventId();
    }
}
