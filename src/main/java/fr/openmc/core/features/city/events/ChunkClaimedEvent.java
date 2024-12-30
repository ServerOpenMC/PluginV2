package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChunkClaimedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter private City city;
    @Getter private Chunk chunk;

    public ChunkClaimedEvent(City city, Chunk chunk) {
        this.city = city;
        this.chunk = chunk;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
