package fr.openmc.core.utils.scheduler;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.Bukkit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Conçu pour régler un problème, les latences du serveur peuvent décaler le temps d'éxécution
 * Donc on refresh tt simplement l'heure de lancement.
 */
public final class OMCScheduler {
    private static final long MAX_REFRESH_TICKS = 20L * 60;

    public static OMCTask runAt(LocalDateTime target, Runnable action) {
        OMCTask task = new OMCTask(target);
        refresh(task, action);
        return task;
    }

    public static OMCTask runLater(long seconds, Runnable action) {
        return runAt(DateUtils.getLocalDateTime().plusSeconds(Math.max(0, seconds)), action);
    }

    private static void refresh(OMCTask task, Runnable action) {
        if (task.isCancelled()) return;

        long remainingMs = DateUtils.getLocalDateTime().until(task.getTarget(), ChronoUnit.MILLIS);
        if (remainingMs <= 0) {
            action.run();
            return;
        }

        long ticks = Math.clamp((long) Math.ceil(remainingMs / 50.0), 1, MAX_REFRESH_TICKS);

        task.setCurrent(Bukkit.getScheduler().runTaskLater(
                OMCPlugin.getInstance(),
                () -> refresh(task, action),
                ticks));
    }
}
