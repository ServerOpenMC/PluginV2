package fr.openmc.core.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class RegionLeaveEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ProtectedRegion region;

    public RegionLeaveEvent(ProtectedRegion region, Player player) {
        super(player);
        this.region = region;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
