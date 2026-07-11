package fr.openmc.core.registry.items.options;

import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import dev.lone.itemsadder.api.Events.FurniturePrePlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public interface UsableBlock {
    default void onFurniturePlace(Player player, FurniturePrePlaceEvent event) {}
    default void onCustomBlockPlace(Player player, CustomBlockPlaceEvent event) {}
    default void onBlockPlace(Player player, BlockPlaceEvent event) {}
}
