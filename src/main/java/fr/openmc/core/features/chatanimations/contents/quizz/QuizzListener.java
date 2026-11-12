package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.utils.cache.CacheQuizCount;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuizzListener implements Listener {

    private final Map<UUID, Integer> correctAnswers = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        ChatAnimation animation = ChatAnimationManager.getActive();
        if (animation == null) return;
        if (!(animation instanceof Quizz quizz)) return;
        if (animation.isFinished()) return;

        String message = PlainTextComponentSerializer.plainText()
                .serialize(event.message())
                .trim()
                .toLowerCase();

        if (quizz.getAnswers().contains(message)) {
            Player winner = event.getPlayer();

            CacheQuizCount.addCorrectAnswer(winner.getUniqueId());

            animation.complete(winner);
            event.setCancelled(true);
        }
    }

    public int getCorrectAnswer(Player player) {
        return correctAnswers.getOrDefault(player.getUniqueId(), 0);
    }
}
