package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Quizz extends ChatAnimation implements Listener {

    private final Component question;
    private final List<String> answers;
    private final CustomLootTable reward;
    private final long time;

    public Quizz(Component question, String... answers) {
        this(question,
                Arrays.asList(answers),
                OMCRegistry.CUSTOM_LOOT_TABLES.QUIZZ,
                30L);
    }

    public Quizz(Component question, List<String> answers, CustomLootTable reward, long time) {
        super("omc:quizz");
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

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!ChatAnimationManager.isActive(this)) return;
        if (isFinished()) return;

        String message = PlainTextComponentSerializer.plainText()
                .serialize(event.message())
                .trim()
                .toLowerCase();

        if (answers.contains(message)) {
            Player winner = event.getPlayer();
            reward.rollLoots(winner);
            complete(winner);
        }
    }
}

