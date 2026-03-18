package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.mecanism.sfx.PlayerCloneNpc;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
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

        // * SFX
        if (PlayerCloneNpc.getCloneNpc(player) == null)
            PlayerCloneNpc.createCloneNpc(player, player.getLocation(), Pose.SITTING);
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            ParticleUtils.sendParticlePacket(player, Particle.FLASH, player.getLocation().add(0, 1, 0));
            ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.REVERSE_PORTAL, 20, 15);
        }, 20);
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

        // * SFX
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            ParticleUtils.sendParticlePacket(player, Particle.FLASH, player.getLocation().add(0, 1, 0));
            ParticleUtils.spawnDispersingParticles(player.getLocation(), Particle.REVERSE_PORTAL, 20, 15);
        }, 20);
    }
}
