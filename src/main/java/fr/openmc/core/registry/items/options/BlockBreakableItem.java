package fr.openmc.core.registry.items.options;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreakableItem {
    default void onBlockBreak(Player player, BlockBreakEvent event) {}
}
