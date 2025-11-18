package fr.openmc.core.features.homes.events;

import fr.openmc.core.features.homes.models.Home;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class HomeTpEvent extends Event {

    private final Home home;
    private final Player player;

    private static final HandlerList HANDLERS = new HandlerList();

    public HomeTpEvent(Home toHome, Player player) {
        this.home = toHome;
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
