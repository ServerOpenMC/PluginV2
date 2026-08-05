package fr.openmc.core.features.chatanimations;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.chatanimations.contents.quizz.Quizz;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Credit(developers = {"iambibi_"})
public class ChatAnimationManager extends Feature implements LoadAfterItemsAdder {

    //todo: changr valeur pour delay entre animationsXXss
    private static final long MIN_DELAY_TICKS = 20 * 60 * 1L; // 20 min
    private static final long MAX_DELAY_TICKS = 20 * 60 * 2L; // 30 min

    private Set<ChatAnimation> ANIMATIONS;

    @Getter
    private static ChatAnimation currentAnimation;
    @Getter
    private static ChatAnimation lastAnimation;
    private BukkitTask scheduleTask;
    private static BukkitTask endAnimationTask;

    @Override
    public void init() {
        ANIMATIONS = new HashSet<>(Set.of(
                new Quizz(TranslationManager.translation("quizz.tech.01"), "python"),
                new Quizz(TranslationManager.translation("quizz.tech.02"), "hypertext markup language", "hyper text markup language"),
                new Quizz(TranslationManager.translation("quizz.tech.03"), "microsoft"),
                new Quizz(TranslationManager.translation("quizz.tech.04"), "linus torvalds", "torvalds"),
                new Quizz(TranslationManager.translation("quizz.tech.05"), "8"),
                new Quizz(TranslationManager.translation("quizz.tech.06"), "python"),
                new Quizz(TranslationManager.translation("quizz.tech.07"), "https"),
                new Quizz(TranslationManager.translation("quizz.tech.08"), "mark zuckerberg", "zuckerberg"),
                new Quizz(TranslationManager.translation("quizz.tech.09"), "mojang"),
                new Quizz(TranslationManager.translation("quizz.tech.10"), "png"),
                new Quizz(TranslationManager.translation("quizz.tech.11"), "chrome", "google chrome"),
                new Quizz(TranslationManager.translation("quizz.tech.12"), "random access memory"),

                new Quizz(TranslationManager.translation("quizz.general.01"), "paris"),
                new Quizz(TranslationManager.translation("quizz.general.02"), "7"),
                new Quizz(TranslationManager.translation("quizz.general.03"), "léonard de vinci", "leonard de vinci", "de vinci"),
                new Quizz(TranslationManager.translation("quizz.general.04"), "pacifique", "océan pacifique", "ocean pacifique"),
                new Quizz(TranslationManager.translation("quizz.general.05"), "1789"),
                new Quizz(TranslationManager.translation("quizz.general.06"), "everest", "mont everest"),
                new Quizz(TranslationManager.translation("quizz.general.07"), "11"),
                new Quizz(TranslationManager.translation("quizz.general.08"), "mars"),
                new Quizz(TranslationManager.translation("quizz.general.09"), "victor hugo", "hugo"),
                new Quizz(TranslationManager.translation("quizz.general.10"), "guépard", "guepard"),
                new Quizz(TranslationManager.translation("quizz.general.11"), "yen"),
                new Quizz(TranslationManager.translation("quizz.general.12"), "366"),
                new Quizz(TranslationManager.translation("quizz.general.13"), "italie"),
                new Quizz(TranslationManager.translation("quizz.general.14"), "beethoven", "ludwig van beethoven"),
                new Quizz(TranslationManager.translation("quizz.general.15"), "nil", "le nil"),
                new Quizz(TranslationManager.translation("quizz.general.16"), "au", "or"),
                new Quizz(TranslationManager.translation("quizz.general.17"), "groenland"),
                new Quizz(TranslationManager.translation("quizz.general.18"), "neil armstrong", "armstrong"),
                new Quizz(TranslationManager.translation("quizz.general.19"), "sumo", "le sumo"),
                new Quizz(TranslationManager.translation("quizz.general.20"), "8", "8 minutes"),
                new Quizz(TranslationManager.translation("quizz.general.21"), "3"),

                new Quizz(TranslationManager.translation("quizz.server.01"), "aywen1"),
                new Quizz(TranslationManager.translation("quizz.server.02"), "notch", "markus persson", "persson"),
                new Quizz(TranslationManager.translation("quizz.server.03"), "aywen"),
                new Quizz(TranslationManager.translation("quizz.server.04"), "jorbani"),
                new Quizz(TranslationManager.translation("quizz.server.05"), "2024")
        ));
        scheduleNext();
    }

    @Override
    public void save() {
        if (scheduleTask != null) scheduleTask.cancel();
        forceStopCurrent();
    }

    private void scheduleNext() {
        long delay = RandomUtils.randomBetween(MIN_DELAY_TICKS, MAX_DELAY_TICKS);

        scheduleTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (isAnimationRunning()) {
                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::scheduleNext, 20 * 30L);
                return;
            }
            startNext();
            scheduleNext();
        }, delay);
    }

    public boolean isAnimationRunning() {
        return currentAnimation != null;
    }

    public void startNext() {
        if (isAnimationRunning()) return;

        List<ChatAnimation> pool = ANIMATIONS.stream()
                .filter(a -> a.equals(lastAnimation))
                .toList();
        if (pool.isEmpty()) return;

        ChatAnimation animation = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

        launch(animation);
    }

    private void launch(ChatAnimation animation) {
        animation.setFinished(false);
        currentAnimation = animation;
        Bukkit.broadcast(currentAnimation.getAnnounceStart());

        long timeBeforeEndTicks = animation.getTimeBeforeEnd() * 20L;
        endAnimationTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (currentAnimation == animation && !animation.isFinished()) {
                animation.complete(null);
            }
        }, timeBeforeEndTicks);
    }

    public static void onAnimationCompleted(ChatAnimation animation, Player winner) {
        if (currentAnimation != animation) return;

        if (endAnimationTask != null) {
            endAnimationTask.cancel();
            endAnimationTask = null;
        }

        if (winner != null) {
            Bukkit.broadcast(TranslationManager.translation("feature.chatanimations.winner",
                    winner.name(), animation.getName(), animation.getDescriptionResult()));
        } else {
            Bukkit.broadcast(TranslationManager.translation("feature.chatanimations.no_winner",
                    animation.getName(), animation.getDescriptionResult()));
        }

        lastAnimation = currentAnimation;
        currentAnimation = null;
    }

    public void forceStopCurrent() {
        if (currentAnimation != null) {
            currentAnimation.complete(null);
        }
    }

    public static boolean isActive(ChatAnimation animation) {
        return currentAnimation.equals(animation);
    }
}
