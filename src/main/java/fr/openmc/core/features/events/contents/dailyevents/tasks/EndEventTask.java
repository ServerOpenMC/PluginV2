package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBroadcast;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasToast;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.stream.Collectors;

public class EndEventTask extends BukkitRunnable {
    private final DailyEvent outgoingEvent;

    public EndEventTask(ScheduleDailyEvent outgoing) {
        this.outgoingEvent = outgoing.getDailyEvent();
    }

    @Override
    public void run() {
        DailyEventsManager.outgoingEvent.getDailyEvent().onEnd().run();

        Collection<Player> receivers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getWorld().getName().equals(outgoingEvent.getWorldEvent()))
                .collect(Collectors.toSet());

        DailyEventsManager.outgoingEvent = null;

        // * Suppression de la l'ambience
        if (outgoingEvent instanceof HasAmbient ambient) {
            ambient.reset(receivers);
        }

        // * Message de fin
        if (outgoingEvent instanceof HasBroadcast broadcast) {
            broadcast.sendEndBroadcast(receivers);
        }

        // * Toast de fin
        if (outgoingEvent instanceof HasToast toast) {
            toast.getEndToastData().send(receivers);
        }
    }
}