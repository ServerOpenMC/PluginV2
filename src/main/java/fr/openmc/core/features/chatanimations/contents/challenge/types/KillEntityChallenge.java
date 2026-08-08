package fr.openmc.core.features.chatanimations.contents.challenge.types;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.contents.challenge.ChallengeAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class KillEntityChallenge extends ChallengeAnimation {
    private final EntityType entityType;
    private final int target;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public KillEntityChallenge(EntityType entityType, int target, long time) {
        this(entityType, target, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public KillEntityChallenge(EntityType entityType, int target, CustomLootTable reward, long time) {
        super(TranslationManager.translation("feature.chatanimations.challenge.kill_entity.name",
                Component.text(target), Component.translatable(entityType.translationKey())), reward, time);
        this.entityType = entityType;
        this.target = target;
    }

    @Override
    public void stop() {
        progress.clear();
    }
}

