package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Quizz extends ChatAnimation implements Listener {

    private final Component question;
    @Getter
    private final List<String> answers;
    @Getter
    private final CustomLootTable reward;
    private final long time;

    public Quizz(Component question, String... answers) {
        this(question,
                Arrays.asList(answers),
                OMCRegistry.CUSTOM_LOOT_TABLES.QUIZZ,
                30L);
    }

    public Quizz(Component question, List<String> answers, CustomLootTable reward, long time) {
        this.question = question;
        this.answers = answers.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        this.reward = reward;
        this.time = time;
    }

    @Override
    public Component getName() {
        return TranslationManager.translation("feature.chatanimations.quizz.name");
    }

    @Override
    public Component getAnnounceStart() {
        return TranslationManager.translation("feature.chatanimations.quizz.announce",
                question,
                Component.text(time, NamedTextColor.GOLD));
    }

    @Override
    public Component getDescriptionResult() {
        return TranslationManager.translation("feature.chatanimations.quizz.result",
                Component.text(answers.getFirst()));
    }

    @Override
    public long getTimeBeforeEnd() {
        return time;
    }
}

