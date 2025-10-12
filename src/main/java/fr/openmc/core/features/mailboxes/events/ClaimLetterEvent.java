package fr.openmc.core.features.mailboxes.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ClaimLetterEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public ClaimLetterEvent(Player player) {
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
