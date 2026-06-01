package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.registries.DreamStructure;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerExitStructureEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final DreamStructure structure;
	private final GeneratedStructure exitedGenerateStructure;
	
	/**
	 * @param player The player who enter in the structure
	 * @param structure The structure where the player enters on
	 * @param exitedGenerateStructure The generated structure instance of the structure where the player leaves
	 */
	public PlayerExitStructureEvent(Player player, DreamStructure structure, GeneratedStructure exitedGenerateStructure) {
		super(player);
		this.structure = structure;
		this.exitedGenerateStructure = exitedGenerateStructure;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}