package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import org.bukkit.scheduler.BukkitRunnable;

public class EndEventTask extends BukkitRunnable {
    @Override
    public void run() {
        DailyEventsManager.outgoingEvent.getDailyEvent().end();
    }
}