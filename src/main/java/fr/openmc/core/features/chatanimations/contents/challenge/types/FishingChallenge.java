package fr.openmc.core.features.chatanimations.contents.challenge.types;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FishingChallenge extends ChallengeAnimation {
    private final int target;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public FishingChallenge(int target, long time) {
        this(target, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public FishingChallenge(int target, CustomLootTable reward, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.fishing.name",
                Component.text(target)), reward, time);
        this.target = target;
    }

    @Override
    public void stop() {
        progress.clear();
    }
}

