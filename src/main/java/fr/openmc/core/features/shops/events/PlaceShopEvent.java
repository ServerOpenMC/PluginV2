package fr.openmc.core.features.shops.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlaceShopEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    public PlaceShopEvent(Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
