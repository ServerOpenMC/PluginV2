package fr.openmc.core.features.homes.events;

import fr.openmc.core.features.homes.models.Home;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class HomeCreateEvent extends Event {

    private final Home home;
    private final UUID ownerUUID;

    private static final HandlerList HANDLERS = new HandlerList();

    public HomeCreateEvent(Home home, UUID ownerUUID) {
        this.home = home;
        this.ownerUUID = ownerUUID;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
