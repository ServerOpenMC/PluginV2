package fr.openmc.core.registry.items.options;

import fr.openmc.api.omcplayer.OMCPlayer;
import org.bukkit.event.player.PlayerInteractEvent;

public interface UsableItem {
    default void onRightClick(OMCPlayer player, PlayerInteractEvent event) {}
    default void onLeftClick(OMCPlayer player, PlayerInteractEvent event) {}
    default void onSneakClick(OMCPlayer player, PlayerInteractEvent event) {}
}
