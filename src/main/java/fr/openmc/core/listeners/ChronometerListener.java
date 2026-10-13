package fr.openmc.core.listeners;

import fr.openmc.api.omcplayer.OMCPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChronometerListener implements Listener {
    @EventHandler
    public void onDisconnection(PlayerQuitEvent e){
        OMCPlayer.of(e.getPlayer()).chronometer().stopAllChronometer(null, null);
    }
}
