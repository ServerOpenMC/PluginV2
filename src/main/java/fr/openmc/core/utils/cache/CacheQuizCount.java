package fr.openmc.core.utils.cache;
import fr.openmc.core.features.chatanimations.contents.quizz.QuizSQLManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheQuizCount {

    private static final TtlCache<UUID, Integer> cache =
            new TtlCache<>(5, TimeUnit.MINUTES);

    public static int getCorrectAnswers(UUID uuid) {
        return cache.getOrCompute(uuid, QuizSQLManager::getCorrectAnswers);
    }

    public static void addCorrectAnswer(UUID uuid) {
        QuizSQLManager.addCorrectAnswer(uuid);
        cache.invalidate(uuid);
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}
