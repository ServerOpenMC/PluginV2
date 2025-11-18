package fr.openmc.api.menulib.events;

import fr.openmc.api.menulib.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OpenMenuEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Menu menu;

    public OpenMenuEvent(Player player, @NotNull Menu menu) {
        super(player);
        this.menu = menu;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
