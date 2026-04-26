package fr.openmc.core.features.cube.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class ExitCubeZoneEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	
	public ExitCubeZoneEvent(@NotNull Player player) {
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