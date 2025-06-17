package fr.openmc.core.features.city.events;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChunkUnclaimedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private City city;
    @Getter
    private int chunkX;
    @Getter
    private int chunkZ;

    public ChunkUnclaimedEvent(City city, int chunkX, int chunkZ) {
        this.city = city;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
