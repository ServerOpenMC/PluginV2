package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class ScheduleNextEventTask extends BukkitRunnable {
    @Override
    public void run() {
        if (DailyEventsManager.incomingEvents.isEmpty()) {
            DailyEventsManager.incomingEvents = DailyEventsManager.loadIncomingEvents();
        }

        DailyEventsManager.outgoingEvent = DailyEventsManager.incomingEvents.removeFirst();

        // * Commencement de l'evenement
        DailyEventsManager.outgoingEvent.getDailyEvent().onStart().run();

        //todo: setup ambient (interface EventAmbient)

        // * Toast de début
        DailyEvent outgoingDailyEvent = DailyEventsManager.outgoingEvent.getDailyEvent();
        if (outgoingDailyEvent instanceof HasToast toast) {
            toast.getStartToastData().send(Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(p -> p.getWorld().getName().equals(outgoingDailyEvent.getWorldEvent()))
                    .collect(Collectors.toSet()));
        }


        // * Programmation de la fin de l'evenement
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            DailyEventsManager.outgoingEvent.getDailyEvent().onEnd().run();
            DailyEventsManager.outgoingEvent = null;

            //todo: remove ambient

            if (outgoingDailyEvent instanceof HasToast toast) {
                toast.getEndToastData().send(Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(p -> p.getWorld().getName().equals(outgoingDailyEvent.getWorldEvent()))
                        .collect(Collectors.toSet()));
            }

        }, DailyEventsManager.outgoingEvent.getDailyEvent().getDuration() * 20L * 20L);

        // * 10 secondes d'attente avant de schedule un autre event (evite que plusieurs events se lancent en meme temps)
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                DailyEventsManager.nextEventTask = DailyEventsManager.scheduleNextEventTask(), 20L * 10);
    }
}
