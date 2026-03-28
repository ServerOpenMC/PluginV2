package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.generation.structures.DreamStructure;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerEnterStructureEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final DreamStructure structure;
	
	/**
	 * @param player The player who enter in the structure
	 * @param structure The structure where the player enters on
	 */
	public PlayerEnterStructureEvent(Player player, DreamStructure structure) {
		super(player);
		this.structure = structure;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
