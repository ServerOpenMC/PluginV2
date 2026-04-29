package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AltarCraftingEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final DreamItem craftedItem;

    /**
     * @param player The player whose dream time has ended
     */
    public AltarCraftingEvent(Player player, DreamItem craftItem) {
        this.player = player;
        this.craftedItem = craftItem;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
