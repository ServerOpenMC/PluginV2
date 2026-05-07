package fr.openmc.core.features.dream.mecanism.rng;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class DreamRngLootEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ItemStack item;
    private final int amount;
    private final Double chance;

    /**
     * @param player The player whose dream time has ended
     */
    public DreamRngLootEvent(Player player, ItemStack item, int amount, Double chance) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.chance = chance;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}