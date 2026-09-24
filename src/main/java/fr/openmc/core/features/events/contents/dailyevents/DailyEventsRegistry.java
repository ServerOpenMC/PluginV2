package fr.openmc.core.features.events.contents.dailyevents;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestManager;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.lifecycle.interfaces.HasFeature;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.Registry;
import fr.openmc.core.registry.features.loading.FeatureEntry;

public class DailyEventsRegistry extends Registry<String, DailyEvent>
        implements KeyedRegistry<String, DailyEvent>  {

    public final MiraculousFishingEvent MIRACULOUS_FISHING = register(new MiraculousFishingEvent());
    public final GoldenHarvestEvent GOLDEN_HARVEST = register(new GoldenHarvestEvent());
    public final BloodyNightEvent BLOODY_NIGHT = register(new BloodyNightEvent());

    @Override
    public void init() {
        // * Register les sous features
        for (DailyEvent event : OMCRegistry.DAILY_EVENTS.values()) {
            if (!(event instanceof HasFeature hasFeature)) continue;

            OMCRegistry.FEATURES.register(FeatureEntry.of(hasFeature::feature));
        }
    }

    @Override
    public String key(DailyEvent registryObject) {
        return registryObject.getEventId();
    }
}
