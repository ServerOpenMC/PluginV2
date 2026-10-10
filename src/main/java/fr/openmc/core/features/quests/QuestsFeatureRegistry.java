package fr.openmc.core.features.quests;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class QuestsFeatureRegistry extends SubRegistry<String, Feature> {
    public final QuestProgressSaveManager QUEST_PROGRESS = register(new QuestProgressSaveManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
