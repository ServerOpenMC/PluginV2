package fr.openmc.core.features.cube.events;

import fr.openmc.core.features.cube.Cube;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CubeDisableBubbleEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Cube cube;

	public CubeDisableBubbleEvent(Cube cube) {
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
