package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.mecanism.altar.AltarRecipes;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class AltarBindEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final DreamItem item;
	private final AltarRecipes recipe;
	private final Location altarLocation;
	
	public AltarBindEvent(Player player, DreamItem item, AltarRecipes recipe, Location altarLocation) {
		super(player);
		this.item = item;
		this.recipe = recipe;
		this.altarLocation = altarLocation;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
