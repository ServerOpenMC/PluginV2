package fr.openmc.core.features.displays.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;

import static fr.openmc.core.features.displays.scoreboards.ScoreboardManager.*;

public class ScoreboardListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (disabledPlayers.contains(player.getUniqueId())) return;

        Scoreboard sb = createNewScoreboard(player);
        player.setScoreboard(sb);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerScoreboards.remove(player.getUniqueId());
    }
}