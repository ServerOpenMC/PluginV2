package fr.openmc.core.features.cube.events;

import fr.openmc.core.features.cube.Cube;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class EnterCubeZoneEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Cube cube;
	
	public EnterCubeZoneEvent(@NotNull Player player, Cube cube) {
		super(player);
		this.cube = cube;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
