package fr.openmc.core.features.dream.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    /**
     * @param player The player whose dream time has ended
     */
    public DreamEndEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
