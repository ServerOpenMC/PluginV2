package fr.openmc.core.features.chatanimations.contents.quizz;

import fr.openmc.core.features.chatanimations.ChatAnimation;
import fr.openmc.core.features.chatanimations.ChatAnimationManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuizzListener implements Listener {
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
            quizz.getReward().rollLoots(winner);
            animation.complete(winner);
        }
    }
}
