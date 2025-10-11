package fr.openmc.core.features.dimsopener.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DimensionUnlockedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String dimensionKey;

    public DimensionUnlockedEvent(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}