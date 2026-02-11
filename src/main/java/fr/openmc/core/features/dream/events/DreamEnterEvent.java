package fr.openmc.core.features.dream.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamEnterEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	
	/**
	 * @param player The player whose enter in his dream
	 */
	public DreamEnterEvent(Player player) {
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
