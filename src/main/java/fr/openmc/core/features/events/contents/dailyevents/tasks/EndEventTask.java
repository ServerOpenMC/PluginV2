package fr.openmc.core.features.events.contents.dailyevents.tasks;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import org.bukkit.scheduler.BukkitRunnable;

public class EndEventTask extends BukkitRunnable {
    @Override
    public void run() {
        OMCRegistry.FEATURES.DAILY_EVENTS.get().getOutgoingEvent().getDailyEvent().end();
    }
}