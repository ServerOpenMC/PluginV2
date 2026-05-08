package fr.openmc.core.features.animations.listeners;

import dev.lone.itemsadder.api.CustomPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.NotInUnitTest;
import fr.openmc.core.features.animations.Animation;
import fr.openmc.core.features.animations.PlayerAnimationInfo;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.openmc.core.features.animations.listeners.EmoteListener.playingAnimations;

public class PlayerFinishJoiningListener implements Listener, NotInUnitTest {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean onGround = player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid();
        if (!(boolean) PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.JOIN_ANIMATION)) return;
        if (player.isFlying() || !onGround || player.getGameMode().equals(GameMode.SPECTATOR)) return;

        playingAnimations.put(player, new PlayerAnimationInfo());
        EmoteListener.setupHead(player);
        player.setInvulnerable(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;

                player.getWorld().playSound(player.getLocation(), "omc_sounds:ambient.join_rift", 1.0f, 1.0f);
                try {
                    CustomPlayer.playEmote(player, Animation.JOIN_RIFT.getNameAnimation());
                } catch (Exception e) {
                    playingAnimations.remove(player);
                    EmoteListener.sendCamera(player, player);
                    player.setInvulnerable(false);
                }
            }
        }.runTaskLater(OMCPlugin.getInstance(), 11L);
    }
}
