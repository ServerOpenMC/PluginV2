package fr.openmc.core.features.events.contents.dailyevents.listeners;

import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBroadcast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!DailyEventsManager.isActiveDailyEvent()) return;

        DailyEvent active = DailyEventsManager.getActiveDailyEvent();

        if (active instanceof HasBroadcast hasBroadcast)
            hasBroadcast.sendStartBroadcast(Set.of(event.getPlayer()));
    }
}
