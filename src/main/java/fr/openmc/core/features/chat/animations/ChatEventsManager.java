package fr.openmc.core.features.chat.animations;

import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

/**
 * Planifie toutes les 20-30 minutes un événement chat aléatoire : quiz ou défi.
 * Alterne quiz / défi à chaque exécution.
 */
public class ChatEventsManager {

    private static final Random RANDOM = new Random();
    private static BukkitTask scheduledTask;
    private static boolean nextIsQuiz = true;

    // --- Configuration de test / production ---
    // Pour passer en production, mettre TEST_MODE = false et ajuster
    // PROD_MIN_MINUTES / PROD_MAX_MINUTES ci-dessous (ex. 20..30 minutes).
    private static final boolean TEST_MODE = false; // <- mettre à false pour la prod
    private static final int TEST_MINUTES = 2; // durée fixe en minutes quand TEST_MODE=true
    private static final int PROD_MIN_MINUTES = 20; // valeur min pour prod
    private static final int PROD_MAX_MINUTES = 30; // valeur max pour prod

    public static void init() {
        scheduleNext();
    }

    private static void scheduleNext() {
        final int minutes;
        if (TEST_MODE) {
            minutes = TEST_MINUTES;
        } else {
            minutes = PROD_MIN_MINUTES + RANDOM.nextInt(PROD_MAX_MINUTES - PROD_MIN_MINUTES + 1);
        }
        long ticks = minutes * 60L * 20L;

        if (scheduledTask != null) {
            scheduledTask.cancel();
        }

        scheduledTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            try {
                if (nextIsQuiz) {
                    ChatAnimations.startQuiz();
                } else {
                    ChatAnimations.startChallenge();
                }
            } finally {
                nextIsQuiz = !nextIsQuiz;
                scheduleNext();
            }
        }, ticks);
    }

    public static void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }
}
