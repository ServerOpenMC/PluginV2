package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.mecanism.altar.AltarRecipes;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class AltarCraftingEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
	private final AltarRecipes recipe;
    private final DreamItem craftedItem;
	
    public AltarCraftingEvent(Player player, AltarRecipes recipe, DreamItem craftItem) {
	    super(player);
		this.recipe = recipe;
        this.craftedItem = craftItem;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
