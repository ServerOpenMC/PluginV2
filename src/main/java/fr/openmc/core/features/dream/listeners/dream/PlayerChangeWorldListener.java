package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.models.DreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onDreamEntrered(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(event.getTo())) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            BossbarManager.removeBossBar(type, player);
        }

        try {
            DreamManager.addDreamPlayer(player, event.getFrom());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        DreamBossBar.addDreamBossBarForPlayer(player, (float) dreamPlayer.getDreamTime() / dreamPlayer.getMaxDreamTime());
    }

    @EventHandler
    public void onDreamLeave(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(event.getFrom())) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            if (type.equals(BossbarsType.DREAM)) continue;

            BossbarManager.addBossBar(type, BossbarManager.bossBarHelp, player);
        }

        BossbarManager.removeBossBar(BossbarsType.DREAM, player);

        DreamManager.removeDreamPlayer(player, event.getFrom());
    }
}
