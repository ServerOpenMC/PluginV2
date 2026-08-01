package fr.openmc.core.features.itemsadder.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class ElevatorBlockListener extends ElevatorBlockManager implements Listener {

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());

        if (!isOnTop(player)) return;

        Location locAfterTP = getNextTop(player);

        if (locAfterTP.equals(player.getLocation())) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.limit.up"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        if (!LocationUtils.isSafeGround(locAfterTP)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        player.teleport(locAfterTP);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());

        if (!isOnTop(player)) return;

        if (event.isSneaking()) return;

        Location locAfterTP = getNextDown(player);

        if (locAfterTP.equals(player.getLocation())) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.limit.down"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        if (!LocationUtils.isSafeGround(locAfterTP)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        player.teleport(locAfterTP);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();

        CustomStack block = null;
        ElevatorColor targetColor = null;

        for (ItemStack item : inv.getMatrix()) {

            if (item == null)
                continue;

            CustomStack custom = CustomStack.byItemStack(item);

            if (custom != null && custom.getNamespacedID().contains("omc_elevator")) {
                block = custom;
                continue;
            }

            for (ElevatorColor color : ElevatorColor.values()) {
                if (item.getType() == color.getDye()) {
                    targetColor = color;
                    break;
                }
            }
        }

        if (block == null) return;

        if (targetColor == null) return;

        if (block.matchNamespacedID(targetColor.getElevator())) return;

        CustomStack result = targetColor.getElevator();

        if (result != null) {
            inv.setResult(result.getItemStack());
        }
    }

    @EventHandler
    public void onElevatorPlaced(CustomBlockPlaceEvent event) {

        if (!event.getNamespacedID().contains("omc_elevator")) return;

        addToColumn(event.getBlock().getLocation());
    }

    @EventHandler
    public void onElevatorRemove(CustomBlockBreakEvent event) {
        if (!event.getNamespacedID().contains("omc_elevator")) return;

        removeToColumn(event.getBlock().getLocation());
    }

}
