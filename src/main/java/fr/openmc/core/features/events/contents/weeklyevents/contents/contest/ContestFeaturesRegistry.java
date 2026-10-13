package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.TradeYMLManager;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class ContestFeaturesRegistry extends SubRegistry<String, Feature> {

    public final ContestPlayerManager CONTEST_PLAYER = register(new ContestPlayerManager());
    public final TradeYMLManager TRADE_YML = register(new TradeYMLManager());


    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
