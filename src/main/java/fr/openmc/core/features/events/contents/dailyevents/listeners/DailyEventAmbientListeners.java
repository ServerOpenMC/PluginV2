package fr.openmc.core.features.events.contents.dailyevents.listeners;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasAmbient;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DailyEventAmbientListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()) return;

        DailyEvent dailyEvent = DailyEventsManager.outgoingEvent.getDailyEvent();

        if (!(dailyEvent instanceof HasAmbient hasAmbient)) return;

        hasAmbient.apply(event.getPlayer());
    }

    @EventHandler
    public void onChangeWorld(PlayerTeleportEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()) return;

        DailyEvent dailyEvent = DailyEventsManager.outgoingEvent.getDailyEvent();

        if (!(dailyEvent instanceof HasAmbient hasAmbient)) return;

        if (dailyEvent.getWorldEvent().equals(event.getFrom().getWorld().getName())) return;
        if (!dailyEvent.getWorldEvent().equals(event.getTo().getWorld().getName())) return;

        hasAmbient.apply(event.getPlayer());
    }
}
