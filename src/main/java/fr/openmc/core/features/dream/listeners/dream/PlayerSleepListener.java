package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.models.db.DBDreamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PlayerSleepListener implements Listener {

    private final Set<UUID> playersDreaming = new HashSet<>();

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) return;

        if (playersDreaming.contains(player.getUniqueId())) return;

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (randomValue < DreamManager.calculateDreamProbability(player)) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NAUSEA,
                20 * 10,
                1,
                false,
                false,
                false
        ));
        playersDreaming.add(player.getUniqueId());
         

    }

    @EventHandler
    public void onNightSkip(TimeSkipEvent event) {
        for (UUID uuid : playersDreaming) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (dbDreamPlayer == null || (dbDreamPlayer.getDreamX() == null || dbDreamPlayer.getDreamY() == null || dbDreamPlayer.getDreamZ() == null)) {
                        DreamManager.tpPlayerDream(player);
                    } else {
                        DreamManager.tpPlayerToLastDreamLocation(player);
                    }
                }
            }.runTaskLater(OMCPlugin.getInstance(), 20L * 5);
        }
        playersDreaming.clear();
    }
}
