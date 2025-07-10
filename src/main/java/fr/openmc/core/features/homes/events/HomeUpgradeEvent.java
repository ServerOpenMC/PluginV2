package fr.openmc.core.features.homes.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class HomeUpgradeEvent extends Event {
    private final Player owner;

    private static final HandlerList HANDLERS = new HandlerList();

    public HomeUpgradeEvent(Player owner) {
        this.owner = owner;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
