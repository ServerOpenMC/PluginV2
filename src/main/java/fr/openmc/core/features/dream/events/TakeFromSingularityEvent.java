package fr.openmc.core.features.dream.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class TakeFromSingularityEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final ItemStack item;
	
	public TakeFromSingularityEvent(Player player, ItemStack item) {
		super(player);
		this.item = item;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
