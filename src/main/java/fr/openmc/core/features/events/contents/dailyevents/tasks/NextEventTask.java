package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class NextEventTask extends BukkitRunnable {
    @Override
    public void run() {
        // * Choix de l'evenement à lancer
        if (DailyEventsManager.incomingEvents.isEmpty()) {
            DailyEventsManager.incomingEvents = DailyEventsManager.loadIncomingEvents();
        }
        DailyEventsManager.outgoingEvent = DailyEventsManager.incomingEvents.removeFirst();

        // * Lancement de l'evenement
        DailyEventsManager.outgoingEvent.getDailyEvent().start();

        // * 10 secondes d'attente avant de schedule un autre event (evite que plusieurs events se lancent en meme temps)
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                DailyEventsManager.nextEventTask = DailyEventsManager.scheduleNextEventTask(), 20L * 10);
    }
}
