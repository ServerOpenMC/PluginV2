package fr.openmc.core.features.cube.listeners;

import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RepulseEffectListener implements Listener {
    public static final Set<UUID> noFallPlayers = new HashSet<>();

    public static void startNoFallParticles(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!noFallPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation().add(0, 1, 0);
                player.getWorld().spawnParticle(Particle.END_ROD, loc, 5, 0.3, 0.5, 0.3, 0.01);
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (noFallPlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
                noFallPlayers.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerLand(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (noFallPlayers.contains(player.getUniqueId()) && player.isOnGround()) {
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    noFallPlayers.remove(player.getUniqueId()), 2L);
        }
    }
}
