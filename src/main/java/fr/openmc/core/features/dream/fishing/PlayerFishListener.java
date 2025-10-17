package fr.openmc.core.features.dream.fishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onStartFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        System.out.println("PlayerFishEvent: " + event.getState());

        FishHook hook = event.getHook();

        switch (event.getState()) {
            case FISHING -> {
                Location hookLoc = hook.getLocation();
                if (hookLoc.getY() > player.getLocation().getY()) return;
                if (hook.getLocation().getY() < CloudFishingManager.Y_CLOUD_FISHING) return;

                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                    if (!hook.isValid()) return;

                    stopHookAtY(hook, hook.getLocation().getY());
                    CloudFishingManager.simulateDreamFishing(player, hook);
                }, 5L);
            }

            case REEL_IN -> {
                System.out.println("REEL_IN");
                if (CloudFishingManager.getHookedPlayers().containsKey(player.getUniqueId())) {
                    CloudFishingManager.getHookedPlayers().get(player.getUniqueId()).endBite();

                    player.sendMessage("pechéééééé");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                }
            }
        }
    }

    private void stopHookAtY(FishHook hook, double y) {
        hook.setGravity(false);
        hook.setVelocity(new Vector(0, 0, 0));

        Location fixedLoc = hook.getLocation().clone();
        fixedLoc.setY(y);
        hook.teleport(fixedLoc);

        hook.setGlowing(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!hook.isValid()) {
                    cancel();
                    return;
                }

                Location loc = hook.getLocation();
                if (Math.abs(loc.getY() - y) > 0.05) {
                    loc.setY(y);
                    hook.teleport(loc);
                    hook.setVelocity(new Vector(0, 0, 0));
                }
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
    }
}
