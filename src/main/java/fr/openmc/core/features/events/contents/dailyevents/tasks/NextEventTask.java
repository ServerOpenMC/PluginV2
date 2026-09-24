package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class NextEventTask extends BukkitRunnable {
    private final DailyEventsManager dailyEventsManager = OMCRegistry.FEATURES.DAILY_EVENTS.get();

    @Override
    public void run() {
        // * Choix de l'evenement à lancer
        if (dailyEventsManager.getIncomingEvents().isEmpty()) {
            dailyEventsManager.setIncomingEvents(dailyEventsManager.loadIncomingEvents());
        }
        dailyEventsManager.setOutgoingEvent(dailyEventsManager.removeFirstIncomingEvent());

        // * Lancement de l'evenement
        dailyEventsManager.getOutgoingEvent().getDailyEvent().start();

        // * 10 secondes d'attente avant de schedule un autre event (evite que plusieurs events se lancent en meme temps)
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                dailyEventsManager.setNextEventTask(dailyEventsManager.scheduleNextEventTask()), 20L * 10);
    }
}
