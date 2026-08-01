package fr.openmc.core.features.itemsadder.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ElevatorBlockListener extends ElevatorBlockManager implements Listener {



    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (!isOnTop(player)) return;

        Location locAfterTP = getNextTop(player);

        if (locAfterTP.equals(player.getLocation())) {
            // TODO message au joueur pour dire qu'il peut pas tp car pas plus haut
            return;
        }

        if (!LocationUtils.isSafeGround(locAfterTP)) {
            // TODO message au joueur pour dire qu'il peut pas tp
            return;
        }

        event.setCancelled(true);
        player.teleport(locAfterTP);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!isOnTop(player)) return;

        Location locAfterTP = getNextDown(player);

        if (locAfterTP.equals(player.getLocation())) {
            // TODO message au joueur pour dire qu'il peut pas tp car pas plus bas
            return;
        }

        if (!LocationUtils.isSafeGround(locAfterTP)) {
            // TODO message au joueur pour dire qu'il peut pas tp car pas safe
            return;
        }

        event.setCancelled(true);
        player.teleport(locAfterTP);
    }

    @EventHandler
    public void onElevatorPlaced(CustomBlockPlaceEvent event) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());

        if (!customBlock.matchNamespacedID(OMCRegistry.CUSTOM_ITEMS.ELEVATOR_GREY.getCustomStack())) return;

        addToColumn(customBlock.getBlock().getLocation());
    }

    @EventHandler
    public void onElevatorRemove(CustomBlockBreakEvent event) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(event.getBlock());

        if (!customBlock.matchNamespacedID(OMCRegistry.CUSTOM_ITEMS.ELEVATOR_GREY.getCustomStack())) return;

        removeToColumn(customBlock.getBlock().getLocation());
    }

}
