package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.mecanism.sfx.PlayerCloneNpc;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerSleepListener implements Listener {

    private final Set<Player> isPlayerSleeping = new HashSet<>();
    private final int DREAM_TELEPORT_DELAY = 20 * 3;

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) return;

        if (isPlayerSleeping.contains(player)) return;
        isPlayerSleeping.add(player);
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        isPlayerSleeping.remove(event.getPlayer());
    }

    @EventHandler
    public void onNightSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            if (isPlayerSleeping.isEmpty()) {
                return;
            }
            for (Player player : isPlayerSleeping) {
                if (ThreadLocalRandom.current().nextDouble() < DreamManager.calculateDreamProbability(player)) {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.NAUSEA,
                            DREAM_TELEPORT_DELAY,
                            1,
                            false,
                            false,
                            true
                    ));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerCloneNpc.createCloneNpc(player, player.getLocation(), Pose.SLEEPING);
                            DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);
                            if(dbDreamPlayer ==null||(dbDreamPlayer.getDreamX()==null||dbDreamPlayer.getDreamY()==null||dbDreamPlayer.getDreamZ()==null)) {
                                DreamManager.tpPlayerDream(player);
                            } else {
                                DreamManager.tpPlayerToLastDreamLocation(player);
                            }
                        }
                    }.runTaskLater(OMCPlugin.getInstance(), DREAM_TELEPORT_DELAY);
                }
            }

            isPlayerSleeping.clear();
        }
    }
}
