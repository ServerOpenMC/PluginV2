package fr.openmc.core.listeners;

import fr.openmc.core.utils.chronometer.Chronometer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChronometerListener implements Listener {
    @EventHandler
    public void onDisconnection(PlayerQuitEvent e){
        Chronometer.stopAllChronometer(e.getPlayer(), null, "%null%");
    }
}
