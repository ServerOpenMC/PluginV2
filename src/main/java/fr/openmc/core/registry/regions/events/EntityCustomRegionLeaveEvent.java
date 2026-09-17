package fr.openmc.core.registry.regions.events;

import fr.openmc.core.registry.regions.CustomRegion;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityCustomRegionLeaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Entity entity;
    @Getter
    private final CustomRegion region;

    public EntityCustomRegionLeaveEvent(Entity entity, CustomRegion region) {
        this.entity = entity;
        this.region = region;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
