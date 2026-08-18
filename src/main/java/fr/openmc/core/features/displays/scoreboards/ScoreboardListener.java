package fr.openmc.core.features.displays.scoreboards;

import fr.openmc.api.scoreboard.SternalBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    private final ScoreboardManager manager;
    public ScoreboardListener(ScoreboardManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SternalBoard board = manager.boardCache.find(player.getUniqueId());

        if (board == null) manager.createNewBoard(player);
        else manager.updateBoard(player, board);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        manager.cleanupPlayer(event.getPlayer());
    }
}