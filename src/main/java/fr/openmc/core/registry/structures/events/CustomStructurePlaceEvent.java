package fr.openmc.core.registry.structures.events;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.registry.structures.CustomStructure;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CustomStructurePlaceEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final UUID placementId;
    @Getter
    private final CustomStructure structure;
    @Getter
    private final BlockVector3 min;
    @Getter
    private final BlockVector3 max;
    @Getter
    private final World world;

    public CustomStructurePlaceEvent(UUID placementId, CustomStructure structure, BlockVector3 min, BlockVector3 max, World world) {
        this.placementId = placementId;
        this.structure = structure;
        this.min = min;
        this.max = max;
        this.world = world;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
