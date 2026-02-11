package fr.openmc.core.features.dream.events;

import fr.openmc.core.features.dream.mecanism.altar.AltarRecipes;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AltarCraftingEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
	private final AltarRecipes recipe;
    private final DreamItem craftedItem;
	
    public AltarCraftingEvent(Player player, AltarRecipes recipe, DreamItem craftItem) {
        this.player = player;
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
