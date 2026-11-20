package fr.openmc.core.registry.structures.events;

import fr.openmc.core.registry.structures.CustomStructure;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CustomStructureRemoveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final UUID placementId;
    @Getter
    private final CustomStructure structure;

    public CustomStructureRemoveEvent(UUID placementId, CustomStructure structure) {
        this.placementId = placementId;
        this.structure = structure;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
