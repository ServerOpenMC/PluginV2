package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Quizz extends ChatAnimation {
    private final String id;
    private final Component question;
    @Getter
    private final List<String> answers;
    private final long time;

    public Quizz(String id, Component question, String... answers) {
        this(
                id,
                question,
                Arrays.asList(answers),
                OMCRegistry.CUSTOM_LOOT_TABLES.QUIZZ,
                30L
        );
    }

    public Quizz(String id, Component question, List<String> answers, CustomLootTable reward, long time) {
        this.id = id;
        this.question = question;
        this.answers = answers.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        this.reward = reward;
        this.time = time;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.chatanimations.quizz.name");
    }

    @Override
    public Component getAnnounceStart() {
        return TranslationManager.translation("feature.chatanimations.quizz.announce",
                question.color(NamedTextColor.GRAY),
                Component.text(time, NamedTextColor.GOLD));
    }

    @Override
    public Component getDescriptionResult() {
        return TranslationManager.translation("feature.chatanimations.quizz.result",
                Component.text(answers.getFirst(), NamedTextColor.YELLOW));
    }

    @Override
    public long getTimeBeforeEnd() {
        return time;
    }
}

