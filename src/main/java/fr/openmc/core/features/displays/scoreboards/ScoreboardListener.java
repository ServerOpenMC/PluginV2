package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.api.scoreboard.SternalBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.openmc.core.features.displays.scoreboards.ScoreboardManager.*;

public class ScoreboardListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SternalBoard board = boardCache.find(player.getUniqueId());

        if (board == null) {
            createNewBoard(player);
        } else {
            updateBoard(player, board);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cleanupPlayer(event.getPlayer().getUniqueId());
    }
}