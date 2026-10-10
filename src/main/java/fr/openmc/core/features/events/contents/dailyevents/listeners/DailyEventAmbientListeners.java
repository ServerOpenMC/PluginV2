package fr.openmc.core.features.events.contents.dailyevents.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Classe gérant l'application des ambiences dans les événements journalier
 */
public class DailyEventAmbientListeners implements Listener {
    private final DailyEventsManager dailyEventsManager = OMCRegistry.FEATURES.DAILY_EVENTS.get();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!dailyEventsManager.isActiveDailyEvent()) return;
        DailyEvent dailyEvent = dailyEventsManager.getOutgoingEvent().getDailyEvent();

        if (!event.getPlayer().getWorld().getName().equals(dailyEvent.getWorldEvent())) return;
        if (!(dailyEvent instanceof HasAmbient hasAmbient)) return;

        hasAmbient.apply(event.getPlayer(), true);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        if (!dailyEventsManager.isActiveDailyEvent()) return;

        ScheduleDailyEvent scheduleDailyEvent = dailyEventsManager.getOutgoingEvent();

        if (scheduleDailyEvent == null) return;
        DailyEvent dailyEvent = scheduleDailyEvent.getDailyEvent();
        if (dailyEvent == null) return;

        if (!(dailyEvent instanceof HasAmbient hasAmbient)) return;
        if (dailyEvent.getWorldEvent().equals(event.getFrom().getName())) return;
        if (!dailyEvent.getWorldEvent().equals(event.getPlayer().getWorld().getName())) return;

        hasAmbient.apply(event.getPlayer());
    }
}
