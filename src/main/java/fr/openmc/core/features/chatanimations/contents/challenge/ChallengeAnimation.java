package fr.openmc.core.features.chatanimations.contents.challenge;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class ChallengeAnimation extends ChatAnimation {
    private final Component description;
    private final long time;

    public ChallengeAnimation(Component description, long time) {
       this(description, OMCRegistry.CUSTOM_LOOT_TABLES.CHALLENGE, time);
    }

    public ChallengeAnimation(Component description, CustomLootTable reward, long time) {
        this.description = description;
        this.reward = reward;
        this.time = time;
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.chatanimation.challenge.name");
    }

    @Override
    public Component getAnnounceStart() {
        return TranslationManager.translation("feature.chatanimations.challenge.announce",
                description.color(NamedTextColor.GRAY),
                Component.text(time, NamedTextColor.DARK_GREEN));
    }

    @Override
    public Component getDescriptionResult() {
        return TranslationManager.translation("feature.chatanimations.challenge.result",
                description.color(NamedTextColor.GRAY));
    }

    @Override
    public long getTimeBeforeEnd() {
        return time;
    }
}
