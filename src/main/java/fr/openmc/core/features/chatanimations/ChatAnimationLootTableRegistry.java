package fr.openmc.core.features.chatanimations;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeLootTable;
import fr.openmc.core.features.chatanimations.contents.quizz.QuizzLootTable;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.loottable.CustomLootTable;

public class ChatAnimationLootTableRegistry extends SubRegistry<String, CustomLootTable> {
    public final QuizzLootTable QUIZZ = register(new QuizzLootTable());
    public final ChallengeLootTable CHALLENGE = register(new ChallengeLootTable());

    @Override
    public KeyedRegistry<String, ? super CustomLootTable> getParentRegistry() {
        return OMCRegistry.CUSTOM_LOOT_TABLES;
    }
}
