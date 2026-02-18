package fr.openmc.core.registry.items.options;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface UsableItem {
    default void onRightClick(Player player, PlayerInteractEvent event) {}
    default void onLeftClick(Player player, PlayerInteractEvent event) {}
    default void onSneakClick(Player player, PlayerInteractEvent event) {}
}
