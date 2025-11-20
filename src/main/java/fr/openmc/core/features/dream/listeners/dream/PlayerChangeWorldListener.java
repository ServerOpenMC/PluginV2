package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        if (DreamUtils.isDreamWorld(event.getFrom())) return;

        for (BossbarsType type : BossbarsType.values()) {
            BossbarManager.removeBossBar(type, player);
        }

        try {
            DreamManager.addDreamPlayer(player, event.getFrom());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
        if (dreamPlayer == null) return;

        DreamBossBar.addDreamBossBarForPlayer(player, Math.min(1, (float) dreamPlayer.getDreamTime() / dreamPlayer.getMaxDreamTime()));

        player.setFoodLevel(20);
        player.setSaturation(10.0f);
        AttributeInstance inst = player.getAttribute(Attribute.MAX_HEALTH);
        if (inst == null) return;
        player.setHealth(inst.getBaseValue());
    }

    @EventHandler
    public void onDreamLeave(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!DreamUtils.isDreamWorld(event.getFrom())) return;
        if (DreamUtils.isDreamWorld(event.getTo())) return;

        for (BossbarsType type : BossbarsType.values()) {
            if (type.equals(BossbarsType.DREAM)) continue;

            BossbarManager.addBossBar(type, BossbarManager.bossBarHelp, player);
        }

        BossbarManager.removeBossBar(BossbarsType.DREAM, player);

        DreamManager.removeDreamPlayer(player, event.getFrom());
    }
}
