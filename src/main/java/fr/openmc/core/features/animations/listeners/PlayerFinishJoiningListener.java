package fr.openmc.core.features.animations.listeners;

import dev.lone.itemsadder.api.CustomPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.animations.Animation;
import fr.openmc.core.features.animations.PlayerAnimationInfo;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.openmc.core.features.animations.listeners.EmoteListener.playingAnimations;

public class PlayerFinishJoiningListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean onGround = player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid();
        if (player.isFlying() || !onGround || player.getGameMode().equals(GameMode.SPECTATOR)) return;

        playingAnimations.put(player, new PlayerAnimationInfo());
        EmoteListener.setupHead(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player, "omc_sounds:ambient.join_rift", 1.0f, 1.0f);
                CustomPlayer.playEmote(player, Animation.JOIN_RIFT.getNameAnimation());
            }
        }.runTaskLater(OMCPlugin.getInstance(), 11L);
    }
}
