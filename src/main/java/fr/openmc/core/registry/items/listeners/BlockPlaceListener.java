package fr.openmc.core.registry.items.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import dev.lone.itemsadder.api.Events.FurniturePrePlaceEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.items.options.UsableBlock;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class BlockPlaceListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    void onFurniturePlace(FurniturePrePlaceEvent event) {
        Player player = event.getPlayer();

        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(event.getNamespacedID());
        if (item.isEmpty()) return;

        if (item.get() instanceof UsableBlock usable) {
            usable.onFurniturePlace(player, event);
        } else if (item.get() instanceof LootboxBlock lootboxBlock) {
            event.setCancelled(true);
            ItemUtils.removeItemsFromPlayerInventory(player, item.get().getBest(), 1);
            lootboxBlock.getLootbox().open(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onCustomBlockPlace(CustomBlockPlaceEvent event) {
        Player player = event.getPlayer();

        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(event.getNamespacedID());
        if (item.isEmpty()) return;
        if (item.get() instanceof UsableBlock usable) {
            usable.onCustomBlockPlace(player, event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(event.getItemInHand());
        if (item.isEmpty()) return;

        if (item.get() instanceof UsableBlock usable) {
            usable.onBlockPlace(player, event);
        }
    }

}