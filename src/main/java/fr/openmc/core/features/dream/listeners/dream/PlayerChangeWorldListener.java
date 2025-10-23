package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.DreamPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.io.IOException;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onDreamEntrered(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();

        if (!world.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            BossbarManager.removeBossBar(type, player);
        }

        try {
            DreamManager.addDreamPlayer(player);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        DreamBossBar.addDreamBossBarForPlayer(player, (float) dreamPlayer.getDreamTime() / dreamPlayer.getMaxDreamTime());
    }

    @EventHandler
    public void onDreamLeave(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!event.getFrom().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            if (type.equals(BossbarsType.DREAM)) continue;

            BossbarManager.addBossBar(type, BossbarManager.bossBarHelp, player);
        }

        BossbarManager.removeBossBar(BossbarsType.DREAM, player);

        DreamManager.removeDreamPlayer(player);
    }
}
