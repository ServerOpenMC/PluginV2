package fr.openmc.core.features.chatanimations.contents.challenge.types;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeAnimation;
import fr.openmc.core.registry.items.keys.KeyBlock;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlaceBlocksChallenge extends ChallengeAnimation {
    private final KeyBlock keyBlock;
    private final int target;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public PlaceBlocksChallenge(KeyBlock keyBlock, int target, long time) {
        this(keyBlock, target, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public PlaceBlocksChallenge(KeyBlock keyBlock, int target, CustomLootTable reward, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.place_blocks.name",
                Component.text(target), keyBlock.name()), reward, time);
        this.keyBlock = keyBlock;
        this.target = target;
    }

    @Override
    public void stop() {
        progress.clear();
    }
}

