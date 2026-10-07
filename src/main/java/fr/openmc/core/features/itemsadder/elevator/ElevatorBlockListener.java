package fr.openmc.core.features.itemsadder.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class ElevatorBlockListener implements Listener {
    private final ElevatorManager elevatorManager = OMCRegistry.FEATURES.ELEVATOR.get();

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());

        if (!elevatorManager.isOnTop(player)) return;

        if (!elevatorManager.isSafeGround(player.getLocation()))  {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed_1"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        Location locAfterTP = elevatorManager.getNextTop(player);

        if (locAfterTP.equals(player.getLocation())) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.limit.up"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        if (!elevatorManager.isSafeGround(locAfterTP)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed_0"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        player.teleport(locAfterTP);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(player.getUniqueId());

        if (!elevatorManager.isOnTop(player)) return;

        if (!elevatorManager.isSafeGround(player.getLocation()))  {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed_1"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        if (event.isSneaking()) return;

        Location locAfterTP = elevatorManager.getNextDown(player);

        if (locAfterTP.equals(player.getLocation())) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.limit.down"),
                    Prefix.OPENMC, MessageType.WARNING, true);
            return;
        }

        if (!elevatorManager.isSafeGround(locAfterTP)) {
            MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("messages.elevator.obstructed_0"),
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

        if (Arrays.stream(inv.getContents())
                .filter(Objects::nonNull)
                .filter(item -> !item.isEmpty())
                .count() > 2) return;

        for (ItemStack item : inv.getContents()) {

            if (item == null)
                continue;

            CustomStack custom = CustomStack.byItemStack(item);

            if (custom != null && elevatorManager.isElevator(custom)) {
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

        if (block.matchNamespacedID(targetColor.getCustomItem().getCustomStack())) return;

        CustomStack result = targetColor.getCustomItem().getCustomStack();

        if (result != null) {
            inv.setResult(result.getItemStack());
        }
    }

    @EventHandler
    public void onElevatorPlaced(CustomBlockPlaceEvent event) {
        if (!elevatorManager.isElevator(event.getNamespacedID())) return;

        elevatorManager.addToColumn(event.getBlock().getLocation());
    }

    @EventHandler
    public void onElevatorRemove(CustomBlockBreakEvent event) {
        if (!elevatorManager.isElevator(event.getNamespacedID())) return;

        elevatorManager.removeToColumn(event.getBlock().getLocation());
    }

}
