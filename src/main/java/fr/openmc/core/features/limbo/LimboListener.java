package fr.openmc.core.features.limbo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.openmc.core.features.limbo.LimboManager.blockedPlayers;
import static fr.openmc.core.features.limbo.LimboManager.isInLimbo;

public class LimboListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (LimboManager.isInLimbo(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isInLimbo(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getUniqueId())) {
            if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (LimboManager.isInLimbo(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerQuitEvent event) {
        if (LimboManager.isInLimbo(event.getPlayer().getUniqueId())) {
            Player player = event.getPlayer();
            LimboManager.exitLimbo(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandInLimbo(PlayerCommandPreprocessEvent event) {
        ;
        Player player = event.getPlayer();
        if (!LimboManager.isInLimbo(player.getUniqueId())) return;

        String message = event.getMessage().toLowerCase();

        if (!message.equals("/limbo")) {
            event.setCancelled(true);
        }
    }
}
