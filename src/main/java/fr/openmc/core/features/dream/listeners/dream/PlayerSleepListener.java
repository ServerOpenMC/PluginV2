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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerSleepListener implements Listener {

    private final Set<UUID> hasPlayerTried = new HashSet<>();

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) return;

        if (hasPlayerTried.contains(player.getUniqueId())) return;
        hasPlayerTried.add(player.getUniqueId());


        if (ThreadLocalRandom.current().nextDouble() < DreamManager.calculateDreamProbability(player)) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NAUSEA,
                20 * 10,
                1,
                false,
                false,
                false
        ));

        new BukkitRunnable() {
            @Override
            public void run() {
                event.setCancelled(true);
                DBDreamPlayer dbDreamPlayer = DreamManager.getCacheDreamPlayer(player);
                if (dbDreamPlayer == null || (dbDreamPlayer.getDreamX() == null || dbDreamPlayer.getDreamY() == null || dbDreamPlayer.getDreamZ() == null)) {
                    DreamManager.tpPlayerDream(player);
                } else {
                    DreamManager.tpPlayerToLastDreamLocation(player);
                }
            }
            }.runTaskLater(OMCPlugin.getInstance(), 20 * 10);
    }

    @EventHandler
    public void onNightSkip(TimeSkipEvent event) {
        hasPlayerTried.clear();
    }
}
