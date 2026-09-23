package fr.openmc.core.features.events.contents.weeklyevents.contents.contest;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.chat.CityChatManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.CityMilestoneManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.managers.TradeYMLManager;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class ContestFeaturesRegistry extends SubRegistry<String, Feature> {

    public final ContestPlayerManager CONTEST_PLAYER = register(
            new ContestPlayerManager());
    public final TradeYMLManager TRADE_YML = register(
            new TradeYMLManager());


    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
