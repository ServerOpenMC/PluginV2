package fr.openmc.core.features.dream.mecanism.cloudfishing;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onStartFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!DreamUtils.isInDreamWorld(player)) return;

        FishHook hook = event.getHook();

        switch (event.getState()) {
            case FISHING -> {
                if (hook.getLocation().getY() < CloudFishingManager.Y_CLOUD_FISHING) return;

                double targetY = player.getLocation().getY() - 5;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!hook.isValid() || !player.isOnline()) {
                            cancel();
                            return;
                        }

                        double hookY = hook.getLocation().getY();

                        if (hookY <= targetY) {
                            cancel();

                            stopHookAtY(hook, hookY);
                            CloudFishingManager.simulateDreamFishing(player, hook);
                        }

                        if (hook.isOnGround() || hook.isDead()) {
                            cancel();
                        }
                    }
                }.runTaskTimer(OMCPlugin.getInstance(), 0L, 2L);
            }

            case REEL_IN -> {
                if (CloudFishingManager.getHookedPlayers().containsKey(player.getUniqueId())) {
                    CloudFishingManager.getHookedPlayers().get(player.getUniqueId()).endBite();

                    CustomLootTable lootTable = CloudFishingManager.FISHING_LOOT_TABLE;
                    if (lootTable == null) return;

                    List<ItemStack> rewards = lootTable.rollLoots();

                    for (ItemStack item : rewards) {
                        player.getInventory().addItem(item);
                    }

                    MessagesManager.sendMessage(player, Component.text("Tu as pêché §e" + rewards.size() + " §fobjet(s) dans tes rêves !"), Prefix.DREAM, MessageType.SUCCESS, false);
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
