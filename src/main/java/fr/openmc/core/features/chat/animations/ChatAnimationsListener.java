package fr.openmc.core.features.chat.animations;

import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatAnimationsListener implements Listener {

    private final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Exécuter sur le thread principal pour éviter d'appeler l'API Bukkit depuis un thread asynchrone
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> ChatAnimations.processChatAnswer(player, message));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock() == null) return;

        Player player = event.getPlayer();
        // notifyProgress pour compter les blocs minés (tous types concernés)
        ChatAnimations.notifyProgress(player, 1);
        // notifyProgress pour défis par matériau (ex: DIAMOND_ORE)
        ChatAnimations.notifyProgress(player, event.getBlock().getType());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getBlock() == null) return;

        Player player = event.getPlayer();
        ChatAnimations.notifyProgress(player, event.getBlock().getType());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null) return;
        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();
        EntityType type = event.getEntityType();
        ChatAnimations.notifyProgress(killer, type);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getPlayer() == null) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Player player = event.getPlayer();
            ChatAnimations.notifyProgress(player, "FISH");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        boolean previous = wasOnGround.getOrDefault(uuid, isOnGroundLoc(event.getFrom()));
        boolean now = isOnGroundLoc(event.getTo());

        // transition from ground -> air and Y increased => probable jump
        if (previous && !now && event.getTo().getY() > event.getFrom().getY()) {
            ChatAnimations.notifyProgress(player);
        }

        wasOnGround.put(uuid, now);
    }

    private boolean isOnGroundLoc(Location loc) {
        if (loc == null) return false;
        // check block below
        return loc.clone().add(0, -0.1, 0).getBlock().getType().isSolid();
    }
}
