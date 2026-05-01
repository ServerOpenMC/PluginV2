package fr.openmc.core.features.dream.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamEnterEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	
	/**
	 * @param player The player whose enter in his dream
	 */
	public DreamEnterEvent(Player player) {
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
