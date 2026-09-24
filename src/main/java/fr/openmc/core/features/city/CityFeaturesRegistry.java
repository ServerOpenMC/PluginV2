package fr.openmc.core.features.city;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.chat.CityChatManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.CityMilestoneManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.protections.ProtectionsManager;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.registry.features.loading.FeatureEntry;
import fr.openmc.core.registry.features.loading.FeatureLoadingType;

public class CityFeaturesRegistry extends SubRegistry<String, Feature> {
    private final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();
    private final FancyNpcsHook fancyNpcsHook = OMCRegistry.HOOKS.FANCY_NPCS;
    private final ItemsAdderHook itemsAdderHook = OMCRegistry.HOOKS.ITEMS_ADDER;

    public final MayorManager MAYOR = register(
            new MayorManager(cityManager, fancyNpcsHook, itemsAdderHook));
    public final ProtectionsManager PROTECTIONS = register(
            new ProtectionsManager(cityManager));
    public final WarManager WAR = register(
            new WarManager());
    public final CityBankManager CITY_BANK = register(
            new CityBankManager(cityManager, MAYOR));
    public final CityStatisticsManager STATS = register(
            new CityStatisticsManager());
    public final NotationManager NOTATION = register(
            new NotationManager(cityManager));
    public final CityRankManager RANKS = register(
            new CityRankManager());
    public final CityMilestoneManager CITY_MILESTONE = register(
            new CityMilestoneManager(cityManager));
    public final CityChatManager CITY_CHAT = register(
            new CityChatManager());
    public final MascotsManager MASCOTS = register(new MascotsManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
