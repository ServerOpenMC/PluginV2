package fr.openmc.core.features.dream.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class MetalDetectorLootEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final List<ItemStack> loot;

    /**
     * @param player The player who found the loot.
     *  @param loot  The list of ItemStack representing the loot found.
     */
    public MetalDetectorLootEvent(Player player, List<ItemStack> loot) {
        this.player = player;
        this.loot = loot;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
