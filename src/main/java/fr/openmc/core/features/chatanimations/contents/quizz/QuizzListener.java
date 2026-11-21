package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import fr.openmc.core.utils.cache.CacheQuizCount;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.Locale;

public class QuizzListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText()
                .serialize(event.message())
                .trim()
                .toLowerCase(Locale.ROOT);

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            ChatAnimation animation = ChatAnimationManager.getActive();

            if (!(animation instanceof Quizz quizz)) return;
            if (quizz.isFinished()) return;

            if (!quizz.getAnswers().contains(message)) return;

            Player winner = event.getPlayer();

            CacheQuizCount.addCorrectAnswer(winner.getUniqueId());

            quizz.complete(winner);
        });
    }
}

